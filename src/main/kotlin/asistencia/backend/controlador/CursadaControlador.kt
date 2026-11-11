package asistencia.backend.controlador

import asistencia.backend.dto.*
import asistencia.backend.service.CursadaService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/cursadas")
class CursadaControlador(private val cursadaService: CursadaService) {

    // /api/cursadas?docenteId=1
    @GetMapping
    fun listar(@RequestParam(required = false) docenteId: Long?): List<CursadaResponse> =
        cursadaService.listar(docenteId)

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long): CursadaResponse = cursadaService.obtener(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody req: CursadaRequest): CursadaResponse = cursadaService.crear(req)

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody req: CursadaRequest): CursadaResponse =
        cursadaService.actualizar(id, req)

    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Long): ResponseEntity<Void> {
        cursadaService.eliminar(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/planilla")
    fun planilla(@PathVariable id: Long): PlanillaResponse = cursadaService.planilla(id)

    // Una celda de la planilla (se llama en cada click)
    @PutMapping("/{id}/asistencias")
    fun registrar(@PathVariable id: Long, @Valid @RequestBody req: AsistenciaRequest): ResponseEntity<Void> {
        cursadaService.registrar(id, req)
        return ResponseEntity.noContent().build()
    }

    // "Todos presentes" para una fecha
    @PostMapping("/{id}/asistencias/lote")
    fun lote(@PathVariable id: Long, @Valid @RequestBody req: LoteRequest): ResponseEntity<Void> {
        cursadaService.marcarLote(id, req)
        return ResponseEntity.noContent().build()
    }

    // Marcar/desmarcar un día como sin actividad
    @PutMapping("/{id}/dias/{fecha}")
    fun marcarDia(
        @PathVariable id: Long,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fecha: LocalDate,
        @Valid @RequestBody req: DiaRequest
    ): DiaResponse = cursadaService.marcarDia(id, fecha, req)
}
