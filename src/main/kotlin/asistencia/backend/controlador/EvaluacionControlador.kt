package asistencia.backend.controlador

import asistencia.backend.modelo.Evaluacion
import asistencia.backend.service.EvaluacionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/evaluaciones")
class EvaluacionControlador(private val evaluacionService: EvaluacionService) {

    @GetMapping
    fun obtenerEvaluaciones(): List<Evaluacion> {
        return evaluacionService.listarTodas()
    }

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): ResponseEntity<Evaluacion> {
        val evaluacion = evaluacionService.buscarPorId(id)
        return if (evaluacion != null) {
            ResponseEntity.ok(evaluacion)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun crearEvaluacion(@RequestBody evaluacion: Evaluacion): Evaluacion {
        return evaluacionService.guardarEvaluacion(evaluacion)
    }

    @PutMapping("/{id}")
    fun actualizarEvaluacion(
        @PathVariable id: Long,
        @RequestBody evaluacionActualizada: Evaluacion
    ): ResponseEntity<Evaluacion> {
        val evaluacion = evaluacionService.actualizarEvaluacion(id, evaluacionActualizada)
        return if (evaluacion != null) {
            ResponseEntity.ok(evaluacion)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun eliminarEvaluacion(@PathVariable id: Long): ResponseEntity<Void> {
        evaluacionService.eliminarEvaluacion(id)
        return ResponseEntity.noContent().build()
    }
}
