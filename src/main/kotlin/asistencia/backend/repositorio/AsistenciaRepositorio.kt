package asistencia.backend.repositorio

import asistencia.backend.modelo.Asistencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AsistenciaRepositorio : JpaRepository<Asistencia, Long> {
    fun findByCursoId(cursoId: Long): List<Asistencia>
    fun findByEstudianteId(estudianteId: Long): List<Asistencia>
}
