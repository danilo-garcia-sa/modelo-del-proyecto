package asistencia.backend.controlador

import asistencia.backend.dto.EstudianteRequest
import asistencia.backend.dto.EstudianteResponse
import asistencia.backend.service.EstudianteService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/estudiantes")
class EstudianteControlador(private val estudianteService: EstudianteService) {

    @GetMapping
    fun listar(pageable: Pageable): ResponseEntity<Page<EstudianteResponse>> {
        return ResponseEntity.ok(estudianteService.listar(pageable))
    }

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): ResponseEntity<EstudianteResponse> {
        return ResponseEntity.ok(estudianteService.obtener(id))
    }

    @PostMapping
    fun crear(@Valid @RequestBody req: EstudianteRequest): ResponseEntity<EstudianteResponse> {
        val creado = estudianteService.crear(req)
        return ResponseEntity.status(HttpStatus.CREATED).body(creado)
    }

    @DeleteMapping("/{id}")
    fun eliminarEstudiante(@PathVariable id: Long): ResponseEntity<Void> {
        estudianteService.eliminarEstudiante(id)
        return ResponseEntity.noContent().build()
    }
}
