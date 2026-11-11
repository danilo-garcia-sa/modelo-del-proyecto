package asistencia.backend.repositorio

import asistencia.backend.modelo.Asistencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AsistenciaRepositorio : JpaRepository<Asistencia, Long> {
    fun findByCursoId(cursoId: Long): List<Asistencia>
    fun findByEstudianteId(estudianteId: Long): List<Asistencia>
    fun findByCursadaId(cursadaId: Long): List<Asistencia>
    fun findByCursadaIdAndEstudianteIdAndFecha(cursadaId: Long, estudianteId: Long, fecha: LocalDate): List<Asistencia>
    fun deleteByCursadaId(cursadaId: Long)
}
