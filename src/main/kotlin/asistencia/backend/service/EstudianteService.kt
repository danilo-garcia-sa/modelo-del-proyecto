package asistencia.backend.service

import asistencia.backend.dto.EstudianteRequest
import asistencia.backend.dto.EstudianteResponse
import asistencia.backend.modelo.Estudiante
import asistencia.backend.exception.EstudianteNoEncontradoException
import asistencia.backend.repositorio.EstudianteRepositorio
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EstudianteService(private val estudianteRepositorio: EstudianteRepositorio) {

    @Transactional(readOnly = true)
    fun listar(pageable: Pageable): Page<EstudianteResponse> {
        return estudianteRepositorio.findAll(pageable)
            .map { EstudianteResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun listarTodos(): List<Estudiante> {
        return estudianteRepositorio.findAll()
    }

    @Transactional(readOnly = true)
    fun obtener(id: Long): EstudianteResponse {
        val estudiante = estudianteRepositorio.findById(id)
            .orElseThrow { EstudianteNoEncontradoException(id) }
        return EstudianteResponse.fromEntity(estudiante)
    }

    fun buscarPorId(id: Long): Estudiante? {
        return estudianteRepositorio.findById(id).orElse(null)
    }

    @Transactional
    fun crear(req: EstudianteRequest): EstudianteResponse {
        val nuevoEstudiante = Estudiante().apply {
            nombre = req.nombre
            apellido = req.apellido
            dni = req.dni
            fotoUrl = req.fotoUrl
        }
        val guardado = estudianteRepositorio.save(nuevoEstudiante)
        return EstudianteResponse.fromEntity(guardado)
    }

    fun guardarEstudiante(estudiante: Estudiante): Estudiante {
        return estudianteRepositorio.save(estudiante)
    }

    @Transactional
    fun eliminarEstudiante(id: Long) {
        if (!estudianteRepositorio.existsById(id)) {
            throw EstudianteNoEncontradoException(id)
        }
        estudianteRepositorio.deleteById(id)
    }
}
