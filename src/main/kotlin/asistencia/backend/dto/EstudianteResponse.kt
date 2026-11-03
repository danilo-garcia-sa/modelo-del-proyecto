package asistencia.backend.dto

import asistencia.backend.modelo.Estudiante

data class EstudianteResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val fotoUrl: String?
) {
    companion object {
        fun fromEntity(e: Estudiante): EstudianteResponse =
            EstudianteResponse(
                id = e.id ?: 0L,
                nombre = e.nombre,
                apellido = e.apellido,
                dni = e.dni,
                fotoUrl = e.fotoUrl
            )
    }
}
