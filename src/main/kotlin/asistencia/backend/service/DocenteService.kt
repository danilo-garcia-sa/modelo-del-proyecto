package asistencia.backend.service

import asistencia.backend.modelo.Docente
import asistencia.backend.repositorio.DocenteRepositorio
import org.springframework.stereotype.Service

@Service
class DocenteService(private val docenteRepositorio: DocenteRepositorio) {

    fun listarTodos(): List<Docente> {
        return docenteRepositorio.findAll()
    }

    fun buscarPorId(id: Long): Docente? {
        return docenteRepositorio.findById(id).orElse(null)
    }

    fun guardarDocente(docente: Docente): Docente {
        return docenteRepositorio.save(docente)
    }

    fun actualizarDocente(id: Long, docenteActualizado: Docente): Docente? {
        return if (docenteRepositorio.existsById(id)) {
            docenteActualizado.id = id
            docenteRepositorio.save(docenteActualizado)
        } else {
            null
        }
    }

    fun eliminarDocente(id: Long) {
        docenteRepositorio.deleteById(id)
    }
}
