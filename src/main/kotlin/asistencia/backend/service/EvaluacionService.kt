package asistencia.backend.service

import asistencia.backend.modelo.Evaluacion
import asistencia.backend.repositorio.EvaluacionRepositorio
import org.springframework.stereotype.Service

@Service
class EvaluacionService(private val evaluacionRepositorio: EvaluacionRepositorio) {

    fun listarTodas(): List<Evaluacion> {
        return evaluacionRepositorio.findAll()
    }

    fun buscarPorId(id: Long): Evaluacion? {
        return evaluacionRepositorio.findById(id).orElse(null)
    }

    fun guardarEvaluacion(evaluacion: Evaluacion): Evaluacion {
        return evaluacionRepositorio.save(evaluacion)
    }

    fun actualizarEvaluacion(id: Long, evaluacionActualizada: Evaluacion): Evaluacion? {
        return if (evaluacionRepositorio.existsById(id)) {
            evaluacionActualizada.id = id
            evaluacionRepositorio.save(evaluacionActualizada)
        } else {
            null
        }
    }

    fun eliminarEvaluacion(id: Long) {
        evaluacionRepositorio.deleteById(id)
    }
}
