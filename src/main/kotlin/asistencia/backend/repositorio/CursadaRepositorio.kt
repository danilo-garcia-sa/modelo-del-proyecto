package asistencia.backend.repositorio

import asistencia.backend.modelo.Cursada
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CursadaRepositorio : JpaRepository<Cursada, Long> {
    fun findByDocenteId(docenteId: Long): List<Cursada>
}
