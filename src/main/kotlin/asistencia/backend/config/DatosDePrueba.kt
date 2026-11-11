package asistencia.backend.config

import asistencia.backend.modelo.Asistencia
import asistencia.backend.modelo.Curso
import asistencia.backend.modelo.Docente
import asistencia.backend.modelo.Estudiante
import asistencia.backend.modelo.Evaluacion
import asistencia.backend.modelo.Inscripcion
import asistencia.backend.modelo.Nota
import asistencia.backend.dto.CursadaRequest
import asistencia.backend.repositorio.AsistenciaRepositorio
import asistencia.backend.repositorio.CursadaRepositorio
import asistencia.backend.dto.EvaluacionRequest
import asistencia.backend.dto.ConfiguracionRequest
import asistencia.backend.dto.InstanciaRequest
import asistencia.backend.dto.NotaRequest
import asistencia.backend.service.CalificacionService
import asistencia.backend.service.CursadaService
import asistencia.backend.repositorio.CursoRepositorio
import asistencia.backend.repositorio.DocenteRepositorio
import asistencia.backend.repositorio.EstudianteRepositorio
import asistencia.backend.repositorio.EvaluacionRepositorio
import asistencia.backend.repositorio.InscripcionRepositorio
import asistencia.backend.repositorio.NotaRepositorio
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Carga datos de prueba al arrancar, para tener lo mismo en casa y en la escuela.
 *
 * - Solo corre si anotame.seed.enabled=true (en el perfil "local" ya viene activado;
 *   en el servidor de la escuela se activa con la variable de entorno ANOTAME_SEED_ENABLED=true).
 * - Docentes, cursos, evaluaciones, notas y asistencias se cargan SOLO si su tabla está vacía.
 * - Estudiantes e inscripciones se completan por DNI: se agrega lo que falte, sin duplicar.
 *   (Si borrás a mano un alumno de prueba, vuelve a aparecer en el próximo arranque.)

 * - Cursadas: se crea la que falte (docente + curso), con su calendario y asistencia de los días pasados.
 * - Reiniciar nunca duplica nada.
 */
@Component
class DatosDePrueba(
    @Value("\${anotame.seed.enabled:false}") private val habilitado: Boolean,
    private val docenteRepositorio: DocenteRepositorio,
    private val estudianteRepositorio: EstudianteRepositorio,
    private val cursoRepositorio: CursoRepositorio,
    private val inscripcionRepositorio: InscripcionRepositorio,
    private val evaluacionRepositorio: EvaluacionRepositorio,
    private val notaRepositorio: NotaRepositorio,
    private val asistenciaRepositorio: AsistenciaRepositorio,
    private val cursadaRepositorio: CursadaRepositorio,
    private val cursadaService: CursadaService,
    private val calificacionService: CalificacionService
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(DatosDePrueba::class.java)

    private companion object {
        const val FABRICIO = "32531269"
        const val LUCIA = "45123456"
        const val ALEX = "25482759"
        const val CURSO_PRINCIPAL = "5to A"
    }

    private data class Alumno(val nombre: String, val apellido: String, val dni: String, val curso: String)

    /** Fotos de perfil (carpeta public/fotos del frontend): al alumno número N de la lista le toca gatoN.jpg. */
    private fun fotoDe(alumno: Alumno) = "/fotos/gato${alumnos.indexOf(alumno) + 1}.jpg"

    /** 5 alumnos por curso. Todos los datos (DNI incluidos) son inventados. */
    private val alumnos = listOf(
        Alumno("Fabricio", "Ferrer", FABRICIO, "5to A"),
        Alumno("Lucía", "Gómez", LUCIA, "5to A"),
        Alumno("Alex", "Fernández", ALEX, "5to A"),
        Alumno("Valentina", "Sosa", "41872356", "5to A"),
        Alumno("Mateo", "Acosta", "42518903", "5to A"),

        Alumno("Sofía", "Romero", "43120785", "5to B"),
        Alumno("Thiago", "Benítez", "42987164", "5to B"),
        Alumno("Camila", "Díaz", "44015372", "5to B"),
        Alumno("Bruno", "Herrera", "43654891", "5to B"),
        Alumno("Julieta", "Molina", "42203947", "5to B"),

        Alumno("Lautaro", "Peralta", "43871026", "5to C"),
        Alumno("Martina", "Ríos", "44290518", "5to C"),
        Alumno("Nicolás", "Castro", "42746830", "5to C"),
        Alumno("Agustina", "Ledesma", "43398205", "5to C"),
        Alumno("Franco", "Navarro", "44102679", "5to C")
    )

    @Transactional
    override fun run(args: ApplicationArguments) {
        if (!habilitado) return

        cargarDocentes()
        cargarEstudiantes()
        cargarCursos()
        cargarInscripciones()
        cargarEvaluaciones()
        cargarNotas()
        cargarCursadas()
        cargarCalificacionesDeCursadas()
    }

    private fun cargarDocentes() {
        if (docenteRepositorio.count() > 0) return
        docenteRepositorio.saveAll(
            listOf(
                Docente(nombre = "Joaquín", apellido = "Blanco", email = "jblanco@etec.uba.ar"),
                Docente(nombre = "Tomás", apellido = "Mayorga", email = "tmayorga@etec.uba.ar"),
                Docente(nombre = "Federico", apellido = "Villace", email = "federicovillace@gmail.com")
            )
        )
        log.info("Datos de prueba: docentes cargados")
    }

    /** Crea los alumnos que falten, buscándolos por DNI (así no se duplican al reiniciar). */
    private fun cargarEstudiantes() {
        var nuevos = 0
        for (alumno in alumnos) {
            val existente = estudianteRepositorio.findByDni(alumno.dni)
            if (existente == null) {
                estudianteRepositorio.save(
                    Estudiante(nombre = alumno.nombre, apellido = alumno.apellido, dni = alumno.dni, fotoUrl = fotoDe(alumno))
                )
                nuevos++
            } else if (existente.fotoUrl == null) {
                existente.fotoUrl = fotoDe(alumno)
                estudianteRepositorio.save(existente)
            }
        }
        if (nuevos > 0) log.info("Datos de prueba: $nuevos estudiantes cargados")
    }

    private fun cargarCursos() {
        if (cursoRepositorio.count() > 0) return
        val cursos = listOf(
            Triple("5to A", 2026, "jblanco@etec.uba.ar"),
            Triple("5to B", 2026, "tmayorga@etec.uba.ar"),
            Triple("5to C", 2026, "federicovillace@gmail.com")
        ).map { (nombre, anio, emailDocente) ->
            Curso(nombre = nombre, anioLectivo = anio, docente = docenteRepositorio.findByEmail(emailDocente))
        }
        cursoRepositorio.saveAll(cursos)
        log.info("Datos de prueba: cursos cargados")
    }

    /** Inscribe a cada alumno en su curso si todavía no lo está (5 alumnos por curso). */
    private fun cargarInscripciones() {
        val cursos = cursoRepositorio.findAll().associateBy { it.nombre }
        var nuevas = 0
        for (alumno in alumnos) {
            val curso = cursos[alumno.curso] ?: continue
            val estudiante = estudianteRepositorio.findByDni(alumno.dni) ?: continue
            val cursoId = curso.id ?: continue
            val estudianteId = estudiante.id ?: continue
            if (!inscripcionRepositorio.existsByEstudianteIdAndCursoId(estudianteId, cursoId)) {
                inscripcionRepositorio.save(Inscripcion(estudiante = estudiante, curso = curso))
                nuevas++
            }
        }
        if (nuevas > 0) log.info("Datos de prueba: $nuevas inscripciones cargadas")
    }

    private fun cargarEvaluaciones() {
        if (evaluacionRepositorio.count() > 0) return
        val curso = cursoPrincipal() ?: return
        evaluacionRepositorio.saveAll(
            listOf(
                Evaluacion(titulo = "Guía 1", descripcion = "Ejercicios de repaso", curso = curso),
                Evaluacion(titulo = "Control 1", descripcion = "Control escrito individual", curso = curso),
                Evaluacion(titulo = "Proyecto", descripcion = "Proyecto integrador", curso = curso)
            )
        )
        log.info("Datos de prueba: evaluaciones cargadas")
    }

    private fun cargarNotas() {
        if (notaRepositorio.count() > 0) return
        val curso = cursoPrincipal() ?: return
        val evaluaciones = evaluacionRepositorio.findByCursoId(curso.id!!).associateBy { it.titulo }

        val titulos = listOf("Guía 1", "Control 1", "Proyecto")
        val valoresPorDni = mapOf(
            FABRICIO to listOf(9.5, 8.7, 9.1),
            LUCIA to listOf(8.2, 9.0, 8.6),
            ALEX to listOf(7.8, 8.4, 8.1)
        )

        val notas = mutableListOf<Nota>()
        for ((dni, valores) in valoresPorDni) {
            val estudiante = estudianteRepositorio.findByDni(dni) ?: continue
            titulos.forEachIndexed { i, titulo ->
                val evaluacion = evaluaciones[titulo] ?: return@forEachIndexed
                notas.add(Nota(valor = valores[i], estudiante = estudiante, evaluacion = evaluacion))
            }
        }
        notaRepositorio.saveAll(notas)
        log.info("Datos de prueba: notas cargadas")
    }

    /**
     * Cursadas de ejemplo (docente + curso + día de la semana) con el calendario del año generado.
     * Solo se crean las que faltan; a las recién creadas se les carga asistencia de los días ya pasados.
     */
    private fun cargarCursadas() {
        val inicio = LocalDate.of(2026, 3, 2)
        val fin = LocalDate.of(2026, 12, 11)
        // (email del docente, curso, día de la semana): cada docente da los 3 cursos, cada uno en un día distinto,
        // y ningún curso tiene dos docentes el mismo día.
        val ejemplos = listOf(
            Triple("jblanco@etec.uba.ar", "5to A", DayOfWeek.MONDAY),
            Triple("jblanco@etec.uba.ar", "5to B", DayOfWeek.WEDNESDAY),
            Triple("jblanco@etec.uba.ar", "5to C", DayOfWeek.FRIDAY),
            Triple("federicovillace@gmail.com", "5to A", DayOfWeek.TUESDAY),
            Triple("federicovillace@gmail.com", "5to B", DayOfWeek.THURSDAY),
            Triple("federicovillace@gmail.com", "5to C", DayOfWeek.MONDAY),
            Triple("tmayorga@etec.uba.ar", "5to A", DayOfWeek.THURSDAY),
            Triple("tmayorga@etec.uba.ar", "5to B", DayOfWeek.FRIDAY),
            Triple("tmayorga@etec.uba.ar", "5to C", DayOfWeek.WEDNESDAY)
        )
        val cursos = cursoRepositorio.findAll().associateBy { it.nombre }
        for ((email, nombreCurso, dia) in ejemplos) {
            val docente = docenteRepositorio.findByEmail(email) ?: continue
            val curso = cursos[nombreCurso] ?: continue
            val yaExiste = cursadaRepositorio.findByDocenteId(docente.id!!).any { it.curso?.id == curso.id }
            if (yaExiste) continue
            val creada = cursadaService.crear(
                CursadaRequest(
                    docenteId = docente.id,
                    cursoId = curso.id,
                    diasDeClase = listOf(dia),
                    fechaInicio = inicio,
                    fechaFin = fin
                )
            )
            cargarAsistenciasDe(creada.id)
            log.info("Datos de prueba: cursada ${docente.apellido} - $nombreCurso ($dia) cargada")
        }
    }

    /**
     * Asistencia de ejemplo para los días de clase que ya pasaron (sin contar los sin actividad).
     * Es determinística: ~6% ausentes y ~6% tardes, siempre los mismos.
     */
    private fun cargarAsistenciasDe(cursadaId: Long) {
        val planilla = cursadaService.planilla(cursadaId)
        val cursada = cursadaRepositorio.findById(cursadaId).orElse(null) ?: return
        val hoy = LocalDate.now()
        val asistencias = mutableListOf<Asistencia>()
        for (dia in planilla.dias) {
            if (dia.sinActividad || dia.fecha.isAfter(hoy)) continue
            for (est in planilla.estudiantes) {
                val estudiante = estudianteRepositorio.findById(est.id).orElse(null) ?: continue
                val bucket = Math.floorMod("${est.dni}-${dia.fecha}".hashCode(), 100)
                val (estado, motivo) = when {
                    bucket < 6 -> "AUSENTE" to (if (bucket % 2 == 0) "Enfermedad" else null)
                    bucket < 12 -> "TARDE" to (if (bucket % 2 == 0) "Transporte" else null)
                    else -> "PRESENTE" to null
                }
                asistencias.add(
                    Asistencia(
                        fecha = dia.fecha, estado = estado, justificacion = motivo,
                        estudiante = estudiante, curso = cursada.curso, cursada = cursada
                    )
                )
            }
        }
        asistenciaRepositorio.saveAll(asistencias)
    }

    /**
     * Calificaciones de ejemplo por cursada (solo si la cursada todavía no tiene nada cargado):
     * - Joaquín (5to A): sistema de puntos, meta 144 (24 clases × 6), 3 TPs.
     * - Mayorga (5to A): sistema de notas, con un bimestre y un cuatrimestre cargados.
     */
    private fun cargarCalificacionesDeCursadas() {
        val cursos = cursoRepositorio.findAll().associateBy { it.nombre }
        val cursoA = cursos[CURSO_PRINCIPAL] ?: return

        val joaquin = docenteRepositorio.findByEmail("jblanco@etec.uba.ar")
        val cursadaJoaquin = joaquin?.let { d -> cursadaRepositorio.findByDocenteId(d.id!!).firstOrNull { it.curso?.id == cursoA.id } }
        if (cursadaJoaquin != null && cursadaJoaquin.puntosAprobar == null &&
            calificacionService.obtener(cursadaJoaquin.id!!).evaluaciones.isEmpty()
        ) {
            val id = cursadaJoaquin.id!!
            calificacionService.configurar(id, ConfiguracionRequest(sistema = "PUNTOS", puntosAprobar = 144))
            val tps = listOf("TP 1" to 25, "TP 2" to 25, "TP 3" to 30)
                .map { (titulo, pts) -> calificacionService.crearEvaluacion(id, EvaluacionRequest(titulo, null, pts)) }
            val estudiantes = calificacionService.obtener(id).estudiantes
            estudiantes.forEachIndexed { i, est ->
                tps.forEachIndexed { j, tp ->
                    val max = tp.puntosMaximos ?: 25
                    val puntos = (max - ((i * 3 + j * 5) % 9)).coerceAtLeast(0).toDouble()
                    calificacionService.guardarNota(id, NotaRequest(est.id, tp.id, puntos))
                }
            }
            log.info("Datos de prueba: cursada de Joaquín configurada con puntos y TPs")
        }

        val mayorga = docenteRepositorio.findByEmail("tmayorga@etec.uba.ar")
        val cursadaMayorga = mayorga?.let { d -> cursadaRepositorio.findByDocenteId(d.id!!).firstOrNull { it.curso?.id == cursoA.id } }
        if (cursadaMayorga != null && calificacionService.obtener(cursadaMayorga.id!!).instancias.isEmpty()) {
            val id = cursadaMayorga.id!!
            val estados = listOf("APROBADO", "APROBADO", "EN_PROCESO", "APROBADO", "DESAPROBADO")
            val notas = listOf(9.0, 8.0, 6.0, 7.0, 4.0)
            calificacionService.obtener(id).estudiantes.forEachIndexed { i, est ->
                calificacionService.guardarInstancia(id, InstanciaRequest(est.id, "BIMESTRE_1", null, estados[i % estados.size]))
                calificacionService.guardarInstancia(id, InstanciaRequest(est.id, "CUATRIMESTRE_1", notas[i % notas.size], null))
            }
            log.info("Datos de prueba: instancias cargadas en la cursada de Mayorga")
        }
    }

    private fun cursoPrincipal(): Curso? =
        cursoRepositorio.findAll().firstOrNull { it.nombre == CURSO_PRINCIPAL }
}
