package asistencia.backend.repositorio

import asistencia.backend.modelo.Inscripcion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InscripcionRepositorio : JpaRepository<Inscripcion, Long> {
    fun findByCursoId(cursoId: Long): List<Inscripcion>
    fun findByEstudianteId(estudianteId: Long): List<Inscripcion>
    fun findByEstudianteIdAndCursoId(estudianteId: Long, cursoId: Long): Inscripcion?
    fun existsByEstudianteIdAndCursoId(estudianteId: Long, cursoId: Long): Boolean
}
