package asistencia.backend.controlador

import asistencia.backend.modelo.Asistencia
import asistencia.backend.service.AsistenciaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/asistencias")
class AsistenciaControlador(private val asistenciaService: AsistenciaService) {

    @GetMapping
    fun obtenerAsistencias(): List<Asistencia> {
        return asistenciaService.listarTodas()
    }

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): ResponseEntity<Asistencia> {
        val asistencia = asistenciaService.buscarPorId(id)
        return if (asistencia != null) {
            ResponseEntity.ok(asistencia)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun crearAsistencia(@RequestBody asistencia: Asistencia): Asistencia {
        return asistenciaService.guardarAsistencia(asistencia)
    }

    @PutMapping("/{id}")
    fun actualizarAsistencia(
        @PathVariable id: Long,
        @RequestBody asistenciaActualizada: Asistencia
    ): ResponseEntity<Asistencia> {
        val asistencia = asistenciaService.actualizarAsistencia(id, asistenciaActualizada)
        return if (asistencia != null) {
            ResponseEntity.ok(asistencia)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun eliminarAsistencia(@PathVariable id: Long): ResponseEntity<Void> {
        asistenciaService.eliminarAsistencia(id)
        return ResponseEntity.noContent().build()
    }
}
