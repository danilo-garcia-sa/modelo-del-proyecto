package asistencia.backend.service

import asistencia.backend.dto.EstudianteResponse
import asistencia.backend.exception.CursoNoEncontradoException
import asistencia.backend.exception.EstudianteNoEncontradoException
import asistencia.backend.exception.InscripcionDuplicadaException
import asistencia.backend.exception.InscripcionNoEncontradaException
import asistencia.backend.modelo.Inscripcion
import asistencia.backend.repositorio.CursoRepositorio
import asistencia.backend.repositorio.EstudianteRepositorio
import asistencia.backend.repositorio.InscripcionRepositorio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InscripcionService(
    private val inscripcionRepositorio: InscripcionRepositorio,
    private val cursoRepositorio: CursoRepositorio,
    private val estudianteRepositorio: EstudianteRepositorio
) {

    /** Estudiantes del curso, ordenados por apellido y nombre (como en una lista de asistencia). */
    @Transactional(readOnly = true)
    fun estudiantesDelCurso(cursoId: Long): List<EstudianteResponse> {
        if (!cursoRepositorio.existsById(cursoId)) {
            throw CursoNoEncontradoException(cursoId)
        }
        return inscripcionRepositorio.findByCursoId(cursoId)
            .mapNotNull { it.estudiante }
            .sortedWith(compareBy({ it.apellido.lowercase() }, { it.nombre.lowercase() }))
            .map { EstudianteResponse.fromEntity(it) }
    }

    @Transactional
    fun inscribir(cursoId: Long, estudianteId: Long): EstudianteResponse {
        val curso = cursoRepositorio.findById(cursoId)
            .orElseThrow { CursoNoEncontradoException(cursoId) }
        val estudiante = estudianteRepositorio.findById(estudianteId)
            .orElseThrow { EstudianteNoEncontradoException(estudianteId) }

        if (inscripcionRepositorio.existsByEstudianteIdAndCursoId(estudianteId, cursoId)) {
            throw InscripcionDuplicadaException(estudianteId, cursoId)
        }

        inscripcionRepositorio.save(Inscripcion(estudiante = estudiante, curso = curso))
        return EstudianteResponse.fromEntity(estudiante)
    }

    @Transactional
    fun desinscribir(cursoId: Long, estudianteId: Long) {
        val inscripcion = inscripcionRepositorio.findByEstudianteIdAndCursoId(estudianteId, cursoId)
            ?: throw InscripcionNoEncontradaException(estudianteId, cursoId)
        inscripcionRepositorio.delete(inscripcion)
    }
}
