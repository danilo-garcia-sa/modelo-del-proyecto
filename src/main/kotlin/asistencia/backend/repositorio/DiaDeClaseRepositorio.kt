package asistencia.backend.repositorio

import asistencia.backend.modelo.DiaDeClase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DiaDeClaseRepositorio : JpaRepository<DiaDeClase, Long> {
    fun findByCursadaIdOrderByFecha(cursadaId: Long): List<DiaDeClase>
    fun findByCursadaIdAndFecha(cursadaId: Long, fecha: LocalDate): DiaDeClase?
    fun deleteByCursadaId(cursadaId: Long)
}
