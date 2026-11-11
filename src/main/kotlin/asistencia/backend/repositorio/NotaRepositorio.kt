package asistencia.backend.repositorio

import asistencia.backend.modelo.Nota
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotaRepositorio : JpaRepository<Nota, Long> {
    fun findByEstudianteId(estudianteId: Long): List<Nota>
    fun findByEvaluacionId(evaluacionId: Long): List<Nota>
    fun findByEvaluacionCursoId(cursoId: Long): List<Nota>
    fun findByEvaluacionCursadaId(cursadaId: Long): List<Nota>
    fun findByEstudianteIdAndEvaluacionId(estudianteId: Long, evaluacionId: Long): List<Nota>
    fun deleteByEvaluacionId(evaluacionId: Long)
}
