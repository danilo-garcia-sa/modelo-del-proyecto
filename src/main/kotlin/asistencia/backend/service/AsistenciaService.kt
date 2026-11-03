package asistencia.backend.service

import asistencia.backend.modelo.Asistencia
import asistencia.backend.repositorio.AsistenciaRepositorio
import org.springframework.stereotype.Service

@Service
class AsistenciaService(private val asistenciaRepositorio: AsistenciaRepositorio) {

    fun listarTodas(): List<Asistencia> {
        return asistenciaRepositorio.findAll()
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
