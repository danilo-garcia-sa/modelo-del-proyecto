package asistencia.backend.repositorio

import asistencia.backend.modelo.Evaluacion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EvaluacionRepositorio : JpaRepository<Evaluacion, Long> {
    fun findByCursoId(cursoId: Long): List<Evaluacion>
    fun findByCursadaId(cursadaId: Long): List<Evaluacion>
}
