package asistencia.backend.controlador
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
data class Mensaje(val mesaje: String)
@RestController
@RequestMapping("/api")
class Status {
    @GetMapping
    fun estado(): ResponseEntity<Mensaje>{
        return ResponseEntity.ok(Mensaje(mesaje = "ok"))
    }
}