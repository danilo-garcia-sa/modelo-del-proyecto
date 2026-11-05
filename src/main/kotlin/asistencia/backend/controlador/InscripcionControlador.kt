package asistencia.backend.controlador

import asistencia.backend.dto.EstudianteResponse
import asistencia.backend.service.InscripcionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cursos/{cursoId}/estudiantes")
class InscripcionControlador(private val inscripcionService: InscripcionService) {

    @GetMapping
    fun listarEstudiantes(@PathVariable cursoId: Long): List<EstudianteResponse> {
        return inscripcionService.estudiantesDelCurso(cursoId)
    }

    @PostMapping("/{estudianteId}")
    fun inscribir(
        @PathVariable cursoId: Long,
        @PathVariable estudianteId: Long
    ): ResponseEntity<EstudianteResponse> {
        val inscripto = inscripcionService.inscribir(cursoId, estudianteId)
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripto)
    }

    @DeleteMapping("/{estudianteId}")
    fun desinscribir(
        @PathVariable cursoId: Long,
        @PathVariable estudianteId: Long
    ): ResponseEntity<Void> {
        inscripcionService.desinscribir(cursoId, estudianteId)
        return ResponseEntity.noContent().build()
    }
}
