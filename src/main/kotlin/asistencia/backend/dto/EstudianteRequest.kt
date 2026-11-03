package asistencia.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class EstudianteRequest(
    @field:NotBlank(message = "El nombre no puede estar vacío")
    @field:Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    val nombre: String,

    @field:NotBlank(message = "El apellido no puede estar vacío")
    @field:Size(min = 1, max = 100, message = "El apellido debe tener entre 1 y 100 caracteres")
    val apellido: String,

    @field:NotBlank(message = "El DNI no puede estar vacío")
    @field:Size(min = 7, max = 20, message = "El DNI debe tener entre 7 y 20 caracteres")
    val dni: String,

    val fotoUrl: String? = null
)
