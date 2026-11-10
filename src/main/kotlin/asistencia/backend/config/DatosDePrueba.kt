package asistencia.backend.config

import asistencia.backend.modelo.Asistencia
import asistencia.backend.modelo.Curso
import asistencia.backend.modelo.Docente
import asistencia.backend.modelo.Estudiante
import asistencia.backend.modelo.Evaluacion
import asistencia.backend.modelo.Inscripcion
import asistencia.backend.modelo.Nota
import asistencia.backend.repositorio.AsistenciaRepositorio
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
    private val asistenciaRepositorio: AsistenciaRepositorio
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(DatosDePrueba::class.java)

    private companion object {
        const val FABRICIO = "32531269"
        const val LUCIA = "45123456"
        const val ALEX = "25482759"
        const val CURSO_PRINCIPAL = "5to A"
    }

    private data class Alumno(val nombre: String, val apellido: String, val dni: String, val curso: String)

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
        cargarAsistencias()
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
            if (estudianteRepositorio.findByDni(alumno.dni) == null) {
                estudianteRepositorio.save(
                    Estudiante(nombre = alumno.nombre, apellido = alumno.apellido, dni = alumno.dni)
                )
                nuevos++
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

    /** Últimos 5 días hábiles (hasta hoy). Casi todo PRESENTE, con una ausencia y una tardanza. */
    private fun cargarAsistencias() {
        if (asistenciaRepositorio.count() > 0) return
        val curso = cursoPrincipal() ?: return
        val dias = ultimosDiasHabiles(5)

        // (dni, posición del día) -> (estado, justificación)
        val excepciones = mapOf(
            (LUCIA to 1) to ("AUSENTE" to "Enfermedad"),
            (ALEX to 3) to ("TARDE" to "Transporte")
        )

        val asistencias = mutableListOf<Asistencia>()
        for (dni in listOf(FABRICIO, LUCIA, ALEX)) {
            val estudiante = estudianteRepositorio.findByDni(dni) ?: continue
            dias.forEachIndexed { i, dia ->
                val excepcion = excepciones[dni to i]
                asistencias.add(
                    Asistencia(
                        fecha = dia,
                        estado = excepcion?.first ?: "PRESENTE",
                        justificacion = excepcion?.second,
                        estudiante = estudiante,
                        curso = curso
                    )
                )
            }
        }
        asistenciaRepositorio.saveAll(asistencias)
        log.info("Datos de prueba: asistencias cargadas")
    }

    private fun cursoPrincipal(): Curso? =
        cursoRepositorio.findAll().firstOrNull { it.nombre == CURSO_PRINCIPAL }

    private fun ultimosDiasHabiles(cantidad: Int): List<LocalDate> {
        val dias = mutableListOf<LocalDate>()
        var dia = LocalDate.now()
        while (dias.size < cantidad) {
            if (dia.dayOfWeek != DayOfWeek.SATURDAY && dia.dayOfWeek != DayOfWeek.SUNDAY) {
                dias.add(dia)
            }
            dia = dia.minusDays(1)
        }
        return dias.reversed()
    }
}
