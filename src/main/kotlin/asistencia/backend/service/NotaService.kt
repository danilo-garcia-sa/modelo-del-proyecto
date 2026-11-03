package asistencia.backend.service

import asistencia.backend.modelo.Nota
import asistencia.backend.repositorio.NotaRepositorio
import org.springframework.stereotype.Service

@Service
class NotaService(private val notaRepositorio: NotaRepositorio) {

    fun listarTodas(): List<Nota> {
        return notaRepositorio.findAll()
    }

    fun buscarPorId(id: Long): Nota? {
        return notaRepositorio.findById(id).orElse(null)
    }

    fun guardarNota(nota: Nota): Nota {
        return notaRepositorio.save(nota)
    }

    fun actualizarNota(id: Long, notaActualizada: Nota): Nota? {
        return if (notaRepositorio.existsById(id)) {
            notaActualizada.id = id
            notaRepositorio.save(notaActualizada)
        } else {
            null
        }
    }

    fun eliminarNota(id: Long) {
        notaRepositorio.deleteById(id)
    }
}
