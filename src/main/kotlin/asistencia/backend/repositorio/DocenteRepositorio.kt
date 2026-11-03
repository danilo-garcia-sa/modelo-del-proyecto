package asistencia.backend.repositorio

import asistencia.backend.modelo.Docente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocenteRepositorio : JpaRepository<Docente, Long> {
    fun findByEmail(email: String): Docente?
}
