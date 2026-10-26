package asistencia.backend.repositorio

import asistencia.backend.modelo.Estudiante
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EstudianteRepositorio : JpaRepository<Estudiante, Long> {
    fun findByDni(dni: String): Estudiante?
}
