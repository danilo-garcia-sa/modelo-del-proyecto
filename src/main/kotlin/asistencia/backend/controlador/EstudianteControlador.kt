package asistencia.backend.controlador

import asistencia.backend.modelo.Estudiante
import asistencia.backend.service.EstudianteService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/estudiantes")
class EstudianteControlador(private val estudianteService: EstudianteService) {

    @GetMapping
    fun obtenerEstudiantes(): List<Estudiante> {
        return estudianteService.listarTodos()
    }

    @PostMapping
    fun crearEstudiante(@RequestBody estudiante: Estudiante): Estudiante {
        return estudianteService.guardarEstudiante(estudiante)
    }

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): Estudiante? {
        return estudianteService.buscarPorId(id)
    }
}
