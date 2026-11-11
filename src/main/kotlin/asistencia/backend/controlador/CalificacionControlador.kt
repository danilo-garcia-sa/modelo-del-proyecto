package asistencia.backend.controlador

import asistencia.backend.dto.*
import asistencia.backend.service.CalificacionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cursadas/{id}")
class CalificacionControlador(private val servicio: CalificacionService) {

    @GetMapping("/calificaciones")
    fun obtener(@PathVariable id: Long): CalificacionesResponse = servicio.obtener(id)

    // Sistema de la cursada (NOTAS / PUNTOS) y meta de puntos
    @PutMapping("/configuracion")
    fun configurar(@PathVariable id: Long, @Valid @RequestBody req: ConfiguracionRequest): ResponseEntity<Void> {
        servicio.configurar(id, req)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/evaluaciones")
    @ResponseStatus(HttpStatus.CREATED)
    fun crearEvaluacion(@PathVariable id: Long, @Valid @RequestBody req: EvaluacionRequest): EvaluacionDto =
        servicio.crearEvaluacion(id, req)

    @PutMapping("/evaluaciones/{evaluacionId}")
    fun editarEvaluacion(
        @PathVariable id: Long, @PathVariable evaluacionId: Long, @Valid @RequestBody req: EvaluacionRequest
    ): EvaluacionDto = servicio.editarEvaluacion(id, evaluacionId, req)

    @DeleteMapping("/evaluaciones/{evaluacionId}")
    fun borrarEvaluacion(@PathVariable id: Long, @PathVariable evaluacionId: Long): ResponseEntity<Void> {
        servicio.borrarEvaluacion(id, evaluacionId)
        return ResponseEntity.noContent().build()
    }

    // Puntos de un alumno en una evaluación (valor null = borrar)
    @PutMapping("/notas")
    fun guardarNota(@PathVariable id: Long, @Valid @RequestBody req: NotaRequest): ResponseEntity<Void> {
        servicio.guardarNota(id, req)
        return ResponseEntity.noContent().build()
    }

    // Bimestre (estado) o cuatrimestre (nota entera 1-10)
    @PutMapping("/instancias")
    fun guardarInstancia(@PathVariable id: Long, @Valid @RequestBody req: InstanciaRequest): ResponseEntity<Void> {
        servicio.guardarInstancia(id, req)
        return ResponseEntity.noContent().build()
    }
}
