package asistencia.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.DayOfWeek
import java.time.LocalDate

// ---------- Cursadas ----------

data class CursadaRequest(
    @field:NotNull(message = "Falta el docente")
    val docenteId: Long?,

    @field:NotNull(message = "Falta el curso")
    val cursoId: Long?,

    /** Días de la semana en los que se dicta: MONDAY, TUESDAY, ... */
    val diasDeClase: List<DayOfWeek>?,

    @field:NotNull(message = "Falta la fecha de inicio")
    val fechaInicio: LocalDate?,

    @field:NotNull(message = "Falta la fecha de fin")
    val fechaFin: LocalDate?
)

data class DocenteResumen(val id: Long, val nombre: String, val apellido: String)

data class CursoResumen(val id: Long, val nombre: String, val anioLectivo: Int)

data class CursadaResponse(
    val id: Long,
    val docente: DocenteResumen,
    val curso: CursoResumen,
    val diasDeClase: List<DayOfWeek>,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate
)

// ---------- Planilla de asistencia ----------

data class DiaResponse(
    val fecha: LocalDate,
    val numero: Int,
    val sinActividad: Boolean,
    val motivo: String?
)

data class AsistenciaCelda(
    val estudianteId: Long,
    val fecha: LocalDate,
    val estado: String,
    val justificacion: String?
)

data class PlanillaResponse(
    val cursada: CursadaResponse,
    val dias: List<DiaResponse>,
    val estudiantes: List<EstudianteResponse>,
    val asistencias: List<AsistenciaCelda>
)

/** estado = null borra el registro de ese alumno ese día. */
data class AsistenciaRequest(
    @field:NotNull(message = "Falta el estudiante")
    val estudianteId: Long?,

    @field:NotNull(message = "Falta la fecha")
    val fecha: LocalDate?,

    val estado: String?,

    val justificacion: String? = null
)

/** Marca a todos los alumnos que todavía no tienen registro ese día. */
data class LoteRequest(
    @field:NotNull(message = "Falta la fecha")
    val fecha: LocalDate?,

    @field:NotBlank(message = "Falta el estado")
    val estado: String?
)

data class DiaRequest(
    @field:NotNull(message = "Falta indicar si el día es sin actividad")
    val sinActividad: Boolean?,

    val motivo: String? = null
)
