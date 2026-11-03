package asistencia.backend.controlador

import asistencia.backend.modelo.Curso
import asistencia.backend.service.CursoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cursos")
class CursoControlador(private val cursoService: CursoService) {

    @GetMapping
    fun obtenerCursos(): List<Curso> {
        return cursoService.listarTodos()
    }

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): ResponseEntity<Curso> {
        val curso = cursoService.buscarPorId(id)
        return if (curso != null) {
            ResponseEntity.ok(curso)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun crearCurso(@RequestBody curso: Curso): Curso {
        return cursoService.guardarCurso(curso)
    }

    @PutMapping("/{id}")
    fun actualizarCurso(
        @PathVariable id: Long,
        @RequestBody cursoActualizado: Curso
    ): ResponseEntity<Curso> {
        val curso = cursoService.actualizarCurso(id, cursoActualizado)
        return if (curso != null) {
            ResponseEntity.ok(curso)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun eliminarCurso(@PathVariable id: Long): ResponseEntity<Void> {
        cursoService.eliminarCurso(id)
        return ResponseEntity.noContent().build()
    }
}
