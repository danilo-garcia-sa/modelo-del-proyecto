package asistencia.backend.modelo

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "asistencias")
class Asistencia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var fecha: LocalDate = LocalDate.now(),

    @Column(nullable = false, length = 10)
    var estado: String = "", // Ej: "PRESENTE", "AUSENTE", "TARDE"

    @Column(length = 255)
    var justificacion: String? = null,

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    var estudiante: Estudiante? = null,

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    var curso: Curso? = null
)
