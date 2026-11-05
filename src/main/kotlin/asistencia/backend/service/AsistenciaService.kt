package asistencia.backend.service

import asistencia.backend.modelo.Asistencia
import asistencia.backend.repositorio.AsistenciaRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AsistenciaService(private val asistenciaRepositorio: AsistenciaRepositorio) {

    /** Sin filtros devuelve todo; los filtros (todos opcionales) se pueden combinar. */
    fun listar(cursoId: Long?, estudianteId: Long?, fecha: LocalDate?): List<Asistencia> {
        val base = when {
            cursoId != null -> asistenciaRepositorio.findByCursoId(cursoId)
            estudianteId != null -> asistenciaRepositorio.findByEstudianteId(estudianteId)
            else -> asistenciaRepositorio.findAll()
        }
        return base
            .filter { estudianteId == null || it.estudiante?.id == estudianteId }
            .filter { fecha == null || it.fecha == fecha }
    }

    fun buscarPorId(id: Long): Asistencia? {
        return asistenciaRepositorio.findById(id).orElse(null)
    }

    fun guardarAsistencia(asistencia: Asistencia): Asistencia {
        return asistenciaRepositorio.save(asistencia)
    }

    fun actualizarAsistencia(id: Long, asistenciaActualizada: Asistencia): Asistencia? {
        return if (asistenciaRepositorio.existsById(id)) {
            asistenciaActualizada.id = id
            asistenciaRepositorio.save(asistenciaActualizada)
        } else {
            null
        }
    }

    fun eliminarAsistencia(id: Long) {
        asistenciaRepositorio.deleteById(id)
    }
}
