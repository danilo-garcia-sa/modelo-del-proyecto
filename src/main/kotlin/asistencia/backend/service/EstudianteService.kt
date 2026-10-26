package asistencia.backend.service

import asistencia.backend.modelo.Estudiante
import asistencia.backend.repositorio.EstudianteRepositorio
import org.springframework.stereotype.Service

@Service
class EstudianteService(private val estudianteRepositorio: EstudianteRepositorio) {

    fun listarTodos(): List<Estudiante> {
        return estudianteRepositorio.findAll()
    }

    fun guardarEstudiante(estudiante: Estudiante): Estudiante {
        return estudianteRepositorio.save(estudiante)
    }

    fun buscarPorId(id: Long): Estudiante? {
        return estudianteRepositorio.findById(id).orElse(null)
    }
}
