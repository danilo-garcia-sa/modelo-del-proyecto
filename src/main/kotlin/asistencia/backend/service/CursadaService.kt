package asistencia.backend.service

import asistencia.backend.dto.*
import asistencia.backend.exception.RecursoNoEncontradoException
import asistencia.backend.exception.SolicitudInvalidaException
import asistencia.backend.modelo.Asistencia
import asistencia.backend.modelo.Cursada
import asistencia.backend.modelo.DiaDeClase
import asistencia.backend.repositorio.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class CursadaService(
    private val cursadaRepositorio: CursadaRepositorio,
    private val diaRepositorio: DiaDeClaseRepositorio,
    private val asistenciaRepositorio: AsistenciaRepositorio,
    private val inscripcionRepositorio: InscripcionRepositorio,
    private val docenteRepositorio: DocenteRepositorio,
    private val cursoRepositorio: CursoRepositorio,
    private val estudianteRepositorio: EstudianteRepositorio,
    private val evaluacionRepositorio: EvaluacionRepositorio,
    private val notaRepositorio: NotaRepositorio,
    private val calificacionRepositorio: CalificacionRepositorio
) {

    companion object {
        val ESTADOS = setOf("PRESENTE", "AUSENTE", "TARDE")
        private const val MAX_DIAS_PERIODO = 400L
    }

    // ---------- Cursadas ----------

    @Transactional(readOnly = true)
    fun listar(docenteId: Long?): List<CursadaResponse> {
        val cursadas = if (docenteId != null) cursadaRepositorio.findByDocenteId(docenteId) else cursadaRepositorio.findAll()
        return cursadas
            .sortedWith(compareBy({ it.docente?.apellido }, { it.curso?.nombre }))
            .map { aRespuesta(it) }
    }

    @Transactional(readOnly = true)
    fun obtener(id: Long): CursadaResponse = aRespuesta(buscar(id))

    @Transactional
    fun crear(req: CursadaRequest): CursadaResponse {
        val dias = validarDias(req)
        val (inicio, fin) = validarPeriodo(req)
        val docente = docenteRepositorio.findById(req.docenteId!!).orElseThrow {
            RecursoNoEncontradoException("No existe el docente ${req.docenteId}")
        }
        val curso = cursoRepositorio.findById(req.cursoId!!).orElseThrow {
            RecursoNoEncontradoException("No existe el curso ${req.cursoId}")
        }
        val cursada = cursadaRepositorio.save(
            Cursada(docente = docente, curso = curso, dias = serializarDias(dias), fechaInicio = inicio, fechaFin = fin)
        )
        sincronizarCalendario(cursada)
        return aRespuesta(cursada)
    }

    @Transactional
    fun actualizar(id: Long, req: CursadaRequest): CursadaResponse {
        val cursada = buscar(id)
        val dias = validarDias(req)
        val (inicio, fin) = validarPeriodo(req)
        cursada.docente = docenteRepositorio.findById(req.docenteId!!).orElseThrow {
            RecursoNoEncontradoException("No existe el docente ${req.docenteId}")
        }
        cursada.curso = cursoRepositorio.findById(req.cursoId!!).orElseThrow {
            RecursoNoEncontradoException("No existe el curso ${req.cursoId}")
        }
        cursada.dias = serializarDias(dias)
        cursada.fechaInicio = inicio
        cursada.fechaFin = fin
        cursadaRepositorio.save(cursada)
        sincronizarCalendario(cursada)
        return aRespuesta(cursada)
    }

    @Transactional
    fun eliminar(id: Long) {
        val cursada = buscar(id)
        evaluacionRepositorio.findByCursadaId(id).forEach { ev ->
            notaRepositorio.deleteByEvaluacionId(ev.id!!)
            evaluacionRepositorio.delete(ev)
        }
        calificacionRepositorio.deleteByCursadaId(id)
        asistenciaRepositorio.deleteByCursadaId(id)
        diaRepositorio.deleteByCursadaId(id)
        cursadaRepositorio.delete(cursada)
    }

    // ---------- Planilla ----------

    @Transactional(readOnly = true)
    fun planilla(id: Long): PlanillaResponse {
        val cursada = buscar(id)
        val dias = diaRepositorio.findByCursadaIdOrderByFecha(id)
        val diasSinActividad = dias.filter { it.sinActividad }.map { it.fecha }.toSet()
        val estudiantes = inscripcionRepositorio.findByCursoId(cursada.curso!!.id!!)
            .mapNotNull { it.estudiante }
            .sortedWith(compareBy({ it.apellido.lowercase() }, { it.nombre.lowercase() }))
            .map { EstudianteResponse.fromEntity(it) }
        val asistencias = asistenciaRepositorio.findByCursadaId(id)
            .filter { it.fecha !in diasSinActividad }
            .mapNotNull { a ->
                val estudianteId = a.estudiante?.id ?: return@mapNotNull null
                AsistenciaCelda(estudianteId, a.fecha, a.estado, a.justificacion)
            }
        return PlanillaResponse(
            cursada = aRespuesta(cursada),
            dias = dias.map { DiaResponse(it.fecha, it.numero, it.sinActividad, it.motivo) },
            estudiantes = estudiantes,
            asistencias = asistencias
        )
    }

    /** Guarda (o borra, si estado es null) la asistencia de un alumno en un día. Se llama en cada click. */
    @Transactional
    fun registrar(id: Long, req: AsistenciaRequest) {
        val cursada = buscar(id)
        val fecha = req.fecha!!
        validarDiaTomable(id, fecha)
        val estudianteId = req.estudianteId!!
        val existentes = asistenciaRepositorio.findByCursadaIdAndEstudianteIdAndFecha(id, estudianteId, fecha)

        val estado = req.estado?.trim()?.uppercase()
        if (estado == null) {
            asistenciaRepositorio.deleteAll(existentes)
            return
        }
        validarEstado(estado)

        val estudiante = estudianteRepositorio.findById(estudianteId).orElseThrow {
            RecursoNoEncontradoException("No existe el estudiante $estudianteId")
        }
        if (!inscripcionRepositorio.existsByEstudianteIdAndCursoId(estudianteId, cursada.curso!!.id!!)) {
            throw SolicitudInvalidaException("El estudiante no está inscripto en ${cursada.curso!!.nombre}")
        }
        val justificacion = req.justificacion?.trim()?.takeIf { it.isNotEmpty() && estado != "PRESENTE" }

        val asistencia = existentes.firstOrNull() ?: Asistencia(
            fecha = fecha, estado = estado, estudiante = estudiante, curso = cursada.curso, cursada = cursada
        )
        asistencia.estado = estado
        asistencia.justificacion = justificacion
        asistenciaRepositorio.save(asistencia)
        if (existentes.size > 1) asistenciaRepositorio.deleteAll(existentes.drop(1))
    }

    /** "Todos presentes": marca a los alumnos que todavía no tienen registro ese día. */
    @Transactional
    fun marcarLote(id: Long, req: LoteRequest) {
        val cursada = buscar(id)
        val fecha = req.fecha!!
        validarDiaTomable(id, fecha)
        val estado = req.estado!!.trim().uppercase()
        validarEstado(estado)

        val yaMarcados = asistenciaRepositorio.findByCursadaId(id)
            .filter { it.fecha == fecha }
            .mapNotNull { it.estudiante?.id }
            .toSet()
        val nuevas = inscripcionRepositorio.findByCursoId(cursada.curso!!.id!!)
            .mapNotNull { it.estudiante }
            .filter { it.id !in yaMarcados }
            .map { Asistencia(fecha = fecha, estado = estado, estudiante = it, curso = cursada.curso, cursada = cursada) }
        asistenciaRepositorio.saveAll(nuevas)
    }

    /** Marca o desmarca un día como "sin actividad" (afecta a todo el curso en esa cursada). */
    @Transactional
    fun marcarDia(id: Long, fecha: LocalDate, req: DiaRequest): DiaResponse {
        buscar(id)
        val dia = diaRepositorio.findByCursadaIdAndFecha(id, fecha)
            ?: throw RecursoNoEncontradoException("El $fecha no es un día de clase de esta cursada")
        dia.sinActividad = req.sinActividad!!
        dia.motivo = if (dia.sinActividad) req.motivo?.trim()?.takeIf { it.isNotEmpty() } else null
        diaRepositorio.save(dia)
        return DiaResponse(dia.fecha, dia.numero, dia.sinActividad, dia.motivo)
    }

    // ---------- Calendario ----------

    /**
     * Deja el calendario de la cursada igual a lo que dicen sus días y su período:
     * agrega las fechas que faltan (marcando feriados como sin actividad), quita las que ya no corresponden
     * (junto con la asistencia que tuvieran cargada) y renumera las clases.
     */
    private fun sincronizarCalendario(cursada: Cursada) {
        val id = cursada.id!!
        val dias = parsearDias(cursada.dias).toSet()
        val deseadas = generarFechas(dias, cursada.fechaInicio, cursada.fechaFin).toSet()
        val existentes = diaRepositorio.findByCursadaIdOrderByFecha(id)

        val sobrantes = existentes.filter { it.fecha !in deseadas }
        if (sobrantes.isNotEmpty()) {
            val fechasSobrantes = sobrantes.map { it.fecha }.toSet()
            asistenciaRepositorio.deleteAll(asistenciaRepositorio.findByCursadaId(id).filter { it.fecha in fechasSobrantes })
            diaRepositorio.deleteAll(sobrantes)
        }

        val fechasExistentes = existentes.map { it.fecha }.toSet()
        val nuevos = deseadas.filter { it !in fechasExistentes }.map { f ->
            val feriado = Feriados.nombreDe(f)
            DiaDeClase(cursada = cursada, fecha = f, sinActividad = feriado != null, motivo = feriado)
        }
        diaRepositorio.saveAll(nuevos)

        val todos = diaRepositorio.findByCursadaIdOrderByFecha(id)
        todos.forEachIndexed { i, d -> d.numero = i + 1 }
        diaRepositorio.saveAll(todos)
    }

    fun generarFechas(dias: Set<DayOfWeek>, inicio: LocalDate, fin: LocalDate): List<LocalDate> =
        generateSequence(inicio) { it.plusDays(1) }
            .takeWhile { !it.isAfter(fin) }
            .filter { it.dayOfWeek in dias }
            .toList()

    // ---------- Helpers ----------

    private fun buscar(id: Long): Cursada =
        cursadaRepositorio.findById(id).orElseThrow { RecursoNoEncontradoException("No existe la cursada $id") }

    private fun validarDias(req: CursadaRequest): List<DayOfWeek> {
        val dias = req.diasDeClase?.distinct()?.sortedBy { it.value }.orEmpty()
        if (dias.isEmpty()) throw SolicitudInvalidaException("Elegí al menos un día de la semana")
        return dias
    }

    private fun validarPeriodo(req: CursadaRequest): Pair<LocalDate, LocalDate> {
        val inicio = req.fechaInicio!!
        val fin = req.fechaFin!!
        if (fin.isBefore(inicio)) throw SolicitudInvalidaException("La fecha de fin no puede ser anterior a la de inicio")
        if (ChronoUnit.DAYS.between(inicio, fin) > MAX_DIAS_PERIODO) {
            throw SolicitudInvalidaException("El período no puede superar un año")
        }
        return inicio to fin
    }

    private fun validarEstado(estado: String) {
        if (estado !in ESTADOS) throw SolicitudInvalidaException("Estado inválido: $estado (usar PRESENTE, AUSENTE o TARDE)")
    }

    private fun validarDiaTomable(cursadaId: Long, fecha: LocalDate) {
        val dia = diaRepositorio.findByCursadaIdAndFecha(cursadaId, fecha)
            ?: throw SolicitudInvalidaException("El $fecha no es un día de clase de esta cursada")
        if (dia.sinActividad) throw SolicitudInvalidaException("El $fecha está marcado como sin actividad")
        if (fecha.isAfter(LocalDate.now())) throw SolicitudInvalidaException("No se puede tomar asistencia de una fecha futura")
    }

    private fun serializarDias(dias: List<DayOfWeek>): String = dias.joinToString(",") { it.value.toString() }

    private fun parsearDias(texto: String): List<DayOfWeek> =
        texto.split(",").mapNotNull { it.trim().toIntOrNull() }.map { DayOfWeek.of(it) }

    fun aRespuesta(c: Cursada): CursadaResponse = CursadaResponse(
        id = c.id!!,
        docente = DocenteResumen(c.docente!!.id!!, c.docente!!.nombre, c.docente!!.apellido),
        curso = CursoResumen(c.curso!!.id!!, c.curso!!.nombre, c.curso!!.anioLectivo),
        diasDeClase = parsearDias(c.dias),
        fechaInicio = c.fechaInicio,
        fechaFin = c.fechaFin,
        sistema = c.sistema,
        puntosAprobar = c.puntosAprobar
    )
}
