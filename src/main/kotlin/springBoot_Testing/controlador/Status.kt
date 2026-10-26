package springBoot_Testing.controlador

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class  Mensaje(val message : String)
@RestController
@RequestMapping("/api/")
class Status {

    @GetMapping("/status")
    fun estados(): ResponseEntity<Mensaje>{
        return ResponseEntity.ok(Mensaje("okay"))
    }

}