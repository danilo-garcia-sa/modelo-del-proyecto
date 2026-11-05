package asistencia.backend.service

import asistencia.backend.modelo.Nota
import asistencia.backend.repositorio.NotaRepositorio
import org.springframework.stereotype.Service

@Service
class NotaService(private val notaRepositorio: NotaRepositorio) {

    /** Sin filtros devuelve todo; los filtros (todos opcionales) se pueden combinar. */
    fun listar(cursoId: Long?, evaluacionId: Long?, estudianteId: Long?): List<Nota> {
        val base = when {
            evaluacionId != null -> notaRepositorio.findByEvaluacionId(evaluacionId)
            cursoId != null -> notaRepositorio.findByEvaluacionCursoId(cursoId)
            estudianteId != null -> notaRepositorio.findByEstudianteId(estudianteId)
            else -> notaRepositorio.findAll()
        }
        return base
            .filter { cursoId == null || it.evaluacion?.curso?.id == cursoId }
            .filter { estudianteId == null || it.estudiante?.id == estudianteId }
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
