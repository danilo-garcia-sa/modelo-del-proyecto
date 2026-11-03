package asistencia.backend.service

import asistencia.backend.modelo.Curso
import asistencia.backend.repositorio.CursoRepositorio
import org.springframework.stereotype.Service

@Service
class CursoService(private val cursoRepositorio: CursoRepositorio) {

    fun listarTodos(): List<Curso> {
        return cursoRepositorio.findAll()
    }

    fun buscarPorId(id: Long): Curso? {
        return cursoRepositorio.findById(id).orElse(null)
    }

    fun guardarCurso(curso: Curso): Curso {
        return cursoRepositorio.save(curso)
    }

    fun actualizarCurso(id: Long, cursoActualizado: Curso): Curso? {
        return if (cursoRepositorio.existsById(id)) {
            cursoActualizado.id = id
            cursoRepositorio.save(cursoActualizado)
        } else {
            null
        }
    }

    fun eliminarCurso(id: Long) {
        cursoRepositorio.deleteById(id)
    }
}
