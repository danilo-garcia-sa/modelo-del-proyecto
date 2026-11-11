package asistencia.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class EvaluacionDto(val id: Long, val titulo: String, val descripcion: String?, val puntosMaximos: Int?)

data class NotaDto(val estudianteId: Long, val evaluacionId: Long, val valor: Double)

data class InstanciaDto(val estudianteId: Long, val instancia: String, val nota: Int?, val estado: String?)

data class CalificacionesResponse(
    val cursada: CursadaResponse,
    val puntosPorClase: Int,
    /** Clases con actividad del calendario (las SA no cuentan). */
    val clasesContadas: Int,
    /** Puntos necesarios para aprobar en el sistema de puntos. */
    val metaPuntos: Int,
    val estudiantes: List<EstudianteResponse>,
    val evaluaciones: List<EvaluacionDto>,
    val notas: List<NotaDto>,
    val instancias: List<InstanciaDto>
)

/** sistema = NOTAS o PUNTOS. puntosAprobar = null usa la meta por defecto (144). */
data class ConfiguracionRequest(
    @field:NotBlank(message = "Falta el sistema de calificación")
    val sistema: String?,
    val puntosAprobar: Int? = null
)

data class EvaluacionRequest(
    @field:NotBlank(message = "Falta el título")
    val titulo: String?,
    val descripcion: String? = null,
    val puntosMaximos: Int? = null
)

/** valor = null borra la nota. */
data class NotaRequest(
    @field:NotNull(message = "Falta el estudiante")
    val estudianteId: Long?,
    @field:NotNull(message = "Falta la evaluación")
    val evaluacionId: Long?,
    val valor: Double?
)

/** Bimestre: se manda estado. Cuatrimestre: se manda nota. Sin ninguno de los dos se borra. */
data class InstanciaRequest(
    @field:NotNull(message = "Falta el estudiante")
    val estudianteId: Long?,
    @field:NotBlank(message = "Falta la instancia")
    val instancia: String?,
    val nota: Double? = null,
    val estado: String? = null
)
