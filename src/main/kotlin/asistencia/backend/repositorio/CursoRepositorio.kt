package asistencia.backend.repositorio

import asistencia.backend.modelo.Curso
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CursoRepositorio : JpaRepository<Curso, Long> {
    fun findByAnioLectivo(anioLectivo: Int): List<Curso>
}
