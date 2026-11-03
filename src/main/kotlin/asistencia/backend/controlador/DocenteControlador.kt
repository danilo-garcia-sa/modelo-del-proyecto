package asistencia.backend.controlador

import asistencia.backend.modelo.Docente
import asistencia.backend.service.DocenteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/docentes")
class DocenteControlador(private val docenteService: DocenteService) {

    @GetMapping
    fun obtenerDocentes(): List<Docente> {
        return docenteService.listarTodos()
    }

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): ResponseEntity<Docente> {
        val docente = docenteService.buscarPorId(id)
        return if (docente != null) {
            ResponseEntity.ok(docente)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun crearDocente(@RequestBody docente: Docente): Docente {
        return docenteService.guardarDocente(docente)
    }

    @PutMapping("/{id}")
    fun actualizarDocente(
        @PathVariable id: Long,
        @RequestBody docenteActualizado: Docente
    ): ResponseEntity<Docente> {
        val docente = docenteService.actualizarDocente(id, docenteActualizado)
        return if (docente != null) {
            ResponseEntity.ok(docente)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun eliminarDocente(@PathVariable id: Long): ResponseEntity<Void> {
        docenteService.eliminarDocente(id)
        return ResponseEntity.noContent().build()
    }
}
