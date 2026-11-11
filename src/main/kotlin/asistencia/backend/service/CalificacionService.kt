package asistencia.backend.service

import asistencia.backend.dto.*
import asistencia.backend.exception.RecursoNoEncontradoException
import asistencia.backend.exception.SolicitudInvalidaException
import asistencia.backend.modelo.Calificacion
import asistencia.backend.modelo.Cursada
import asistencia.backend.modelo.Evaluacion
import asistencia.backend.modelo.Nota
import asistencia.backend.repositorio.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CalificacionService(
    private val cursadaRepositorio: CursadaRepositorio,
    private val cursadaService: CursadaService,
    private val diaRepositorio: DiaDeClaseRepositorio,
    private val inscripcionRepositorio: InscripcionRepositorio,
    private val estudianteRepositorio: EstudianteRepositorio,
    private val evaluacionRepositorio: EvaluacionRepositorio,
    private val notaRepositorio: NotaRepositorio,
    private val calificacionRepositorio: CalificacionRepositorio
) {

    companion object {
        const val PUNTOS_POR_CLASE = 6
        /** Meta confirmada con el docente: 144 puntos (24 clases × 6). No se recalcula por clases perdidas (paros, etc.). */
        const val META_PUNTOS_POR_DEFECTO = 144
        val SISTEMAS = setOf("NOTAS", "PUNTOS")
        val BIMESTRES = setOf("BIMESTRE_1", "BIMESTRE_2")
        val CUATRIMESTRES = setOf("CUATRIMESTRE_1", "CUATRIMESTRE_2")
        val ESTADOS = setOf("APROBADO", "EN_PROCESO", "DESAPROBADO")
        const val NOTA_MINIMA = 1
        const val NOTA_MAXIMA = 10
        const val NOTA_APROBACION = 7
    }

    @Transactional(readOnly = true)
    fun obtener(id: Long): CalificacionesResponse {
        val cursada = buscar(id)
        val clases = diaRepositorio.findByCursadaIdOrderByFecha(id).count { !it.sinActividad }
        val estudiantes = inscripcionRepositorio.findByCursoId(cursada.curso!!.id!!)
            .mapNotNull { it.estudiante }
            .sortedWith(compareBy({ it.apellido.lowercase() }, { it.nombre.lowercase() }))
            .map { EstudianteResponse.fromEntity(it) }
        val evaluaciones = evaluacionRepositorio.findByCursadaId(id).sortedBy { it.id }
        val notas = notaRepositorio.findByEvaluacionCursadaId(id).mapNotNull { n ->
            val est = n.estudiante?.id ?: return@mapNotNull null
            val ev = n.evaluacion?.id ?: return@mapNotNull null
            NotaDto(est, ev, n.valor)
        }
        val instancias = calificacionRepositorio.findByCursadaId(id).mapNotNull { c ->
            val est = c.estudiante?.id ?: return@mapNotNull null
            InstanciaDto(est, c.instancia, c.nota, c.estado)
        }
        return CalificacionesResponse(
            cursada = cursadaService.aRespuesta(cursada),
            puntosPorClase = PUNTOS_POR_CLASE,
            clasesContadas = clases,
            metaPuntos = cursada.puntosAprobar ?: META_PUNTOS_POR_DEFECTO,
            estudiantes = estudiantes,
            evaluaciones = evaluaciones.map { EvaluacionDto(it.id!!, it.titulo, it.descripcion, it.puntosMaximos) },
            notas = notas,
            instancias = instancias
        )
    }

    @Transactional
    fun configurar(id: Long, req: ConfiguracionRequest) {
        val cursada = buscar(id)
        val sistema = req.sistema!!.trim().uppercase()
        if (sistema !in SISTEMAS) throw SolicitudInvalidaException("Sistema inválido: $sistema (usar NOTAS o PUNTOS)")
        if (req.puntosAprobar != null && req.puntosAprobar < 1) {
            throw SolicitudInvalidaException("Los puntos para aprobar tienen que ser mayores a 0")
        }
        cursada.sistema = sistema
        cursada.puntosAprobar = req.puntosAprobar
        cursadaRepositorio.save(cursada)
    }

    // ---------- Evaluaciones (sistema de puntos) ----------

    @Transactional
    fun crearEvaluacion(id: Long, req: EvaluacionRequest): EvaluacionDto {
        val cursada = buscar(id)
        validarPuntosMaximos(req.puntosMaximos)
        val ev = evaluacionRepositorio.save(
            Evaluacion(
                titulo = req.titulo!!.trim(),
                descripcion = req.descripcion?.trim()?.takeIf { it.isNotEmpty() },
                curso = cursada.curso,
                cursada = cursada,
                puntosMaximos = req.puntosMaximos
            )
        )
        return EvaluacionDto(ev.id!!, ev.titulo, ev.descripcion, ev.puntosMaximos)
    }

    @Transactional
    fun editarEvaluacion(id: Long, evaluacionId: Long, req: EvaluacionRequest): EvaluacionDto {
        val ev = buscarEvaluacion(id, evaluacionId)
        validarPuntosMaximos(req.puntosMaximos)
        ev.titulo = req.titulo!!.trim()
        ev.descripcion = req.descripcion?.trim()?.takeIf { it.isNotEmpty() }
        ev.puntosMaximos = req.puntosMaximos
        evaluacionRepositorio.save(ev)
        return EvaluacionDto(ev.id!!, ev.titulo, ev.descripcion, ev.puntosMaximos)
    }

    @Transactional
    fun borrarEvaluacion(id: Long, evaluacionId: Long) {
        val ev = buscarEvaluacion(id, evaluacionId)
        notaRepositorio.deleteByEvaluacionId(evaluacionId)
        evaluacionRepositorio.delete(ev)
    }

    /** Guarda (o borra, si valor es null) los puntos de un alumno en una evaluación. */
    @Transactional
    fun guardarNota(id: Long, req: NotaRequest) {
        val cursada = buscar(id)
        val ev = buscarEvaluacion(id, req.evaluacionId!!)
        val estudianteId = req.estudianteId!!
        val existentes = notaRepositorio.findByEstudianteIdAndEvaluacionId(estudianteId, ev.id!!)
        val valor = req.valor
        if (valor == null) {
            notaRepositorio.deleteAll(existentes)
            return
        }
        if (valor < 0) throw SolicitudInvalidaException("Los puntos no pueden ser negativos")
        if (ev.puntosMaximos != null && valor > ev.puntosMaximos!!) {
            throw SolicitudInvalidaException("${ev.titulo} vale como máximo ${ev.puntosMaximos} puntos")
        }
        validarInscripto(cursada, estudianteId)
        val estudiante = estudianteRepositorio.findById(estudianteId).orElseThrow {
            RecursoNoEncontradoException("No existe el estudiante $estudianteId")
        }
        val nota = existentes.firstOrNull() ?: Nota(estudiante = estudiante, evaluacion = ev)
        nota.valor = valor
        notaRepositorio.save(nota)
        if (existentes.size > 1) notaRepositorio.deleteAll(existentes.drop(1))
    }

    // ---------- Instancias (bimestres y cuatrimestres) ----------

    @Transactional
    fun guardarInstancia(id: Long, req: InstanciaRequest) {
        val cursada = buscar(id)
        val instancia = req.instancia!!.trim().uppercase()
        if (instancia !in BIMESTRES && instancia !in CUATRIMESTRES) {
            throw SolicitudInvalidaException("Instancia inválida: $instancia")
        }
        val estudianteId = req.estudianteId!!
        val existente = calificacionRepositorio.findByCursadaIdAndEstudianteIdAndInstancia(id, estudianteId, instancia)

        val estado = req.estado?.trim()?.uppercase()?.takeIf { it.isNotEmpty() }
        val nota = req.nota
        if (estado == null && nota == null) {
            existente?.let { calificacionRepositorio.delete(it) }
            return
        }

        var notaEntera: Int? = null
        if (instancia in BIMESTRES) {
            if (nota != null) throw SolicitudInvalidaException("Los bimestres se cargan como Aprobado, En proceso o Desaprobado")
            if (estado !in ESTADOS) throw SolicitudInvalidaException("Estado inválido: $estado")
        } else {
            if (estado != null) throw SolicitudInvalidaException("Los cuatrimestres se cargan con una nota de $NOTA_MINIMA a $NOTA_MAXIMA")
            if (nota != Math.floor(nota!!)) throw SolicitudInvalidaException("La nota del cuatrimestre no puede tener decimales")
            notaEntera = nota.toInt()
            if (notaEntera !in NOTA_MINIMA..NOTA_MAXIMA) {
                throw SolicitudInvalidaException("La nota tiene que estar entre $NOTA_MINIMA y $NOTA_MAXIMA")
            }
        }

        validarInscripto(cursada, estudianteId)
        val estudiante = estudianteRepositorio.findById(estudianteId).orElseThrow {
            RecursoNoEncontradoException("No existe el estudiante $estudianteId")
        }
        val fila = existente ?: Calificacion(cursada = cursada, estudiante = estudiante, instancia = instancia)
        fila.nota = notaEntera
        fila.estado = estado
        calificacionRepositorio.save(fila)
    }

    // ---------- Helpers ----------

    private fun buscar(id: Long): Cursada =
        cursadaRepositorio.findById(id).orElseThrow { RecursoNoEncontradoException("No existe la cursada $id") }

    private fun buscarEvaluacion(cursadaId: Long, evaluacionId: Long): Evaluacion {
        val ev = evaluacionRepositorio.findById(evaluacionId).orElseThrow {
            RecursoNoEncontradoException("No existe la evaluación $evaluacionId")
        }
        if (ev.cursada?.id != cursadaId) throw SolicitudInvalidaException("La evaluación no pertenece a esta cursada")
        return ev
    }

    private fun validarInscripto(cursada: Cursada, estudianteId: Long) {
        if (!inscripcionRepositorio.existsByEstudianteIdAndCursoId(estudianteId, cursada.curso!!.id!!)) {
            throw SolicitudInvalidaException("El estudiante no está inscripto en ${cursada.curso!!.nombre}")
        }
    }

    private fun validarPuntosMaximos(puntos: Int?) {
        if (puntos != null && puntos < 1) throw SolicitudInvalidaException("Los puntos de una evaluación tienen que ser mayores a 0")
    }
}
