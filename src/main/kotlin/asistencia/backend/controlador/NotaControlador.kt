package asistencia.backend.controlador

import asistencia.backend.modelo.Nota
import asistencia.backend.service.NotaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notas")
class NotaControlador(private val notaService: NotaService) {

    @GetMapping
    fun obtenerNotas(): List<Nota> {
        return notaService.listarTodas()
    }

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): ResponseEntity<Nota> {
        val nota = notaService.buscarPorId(id)
        return if (nota != null) {
            ResponseEntity.ok(nota)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun crearNota(@RequestBody nota: Nota): Nota {
        return notaService.guardarNota(nota)
    }

    @PutMapping("/{id}")
    fun actualizarNota(
        @PathVariable id: Long,
        @RequestBody notaActualizada: Nota
    ): ResponseEntity<Nota> {
        val nota = notaService.actualizarNota(id, notaActualizada)
        return if (nota != null) {
            ResponseEntity.ok(nota)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun eliminarNota(@PathVariable id: Long): ResponseEntity<Void> {
        notaService.eliminarNota(id)
        return ResponseEntity.noContent().build()
    }
}
