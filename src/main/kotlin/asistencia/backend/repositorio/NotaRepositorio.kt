package asistencia.backend.repositorio

import asistencia.backend.modelo.Nota
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotaRepositorio : JpaRepository<Nota, Long> {
    fun findByEstudianteId(estudianteId: Long): List<Nota>
    fun findByEvaluacionId(evaluacionId: Long): List<Nota>
}
