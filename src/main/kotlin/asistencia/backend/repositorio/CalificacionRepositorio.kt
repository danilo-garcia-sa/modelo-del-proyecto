package asistencia.backend.repositorio

import asistencia.backend.modelo.Calificacion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalificacionRepositorio : JpaRepository<Calificacion, Long> {
    fun findByCursadaId(cursadaId: Long): List<Calificacion>
    fun findByCursadaIdAndEstudianteIdAndInstancia(cursadaId: Long, estudianteId: Long, instancia: String): Calificacion?
    fun deleteByCursadaId(cursadaId: Long)
}
