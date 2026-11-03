package asistencia.backend.modelo

import jakarta.persistence.*

@Entity
@Table(name = "evaluaciones")
class Evaluacion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 120)
    var titulo: String = "",

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null,

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    var curso: Curso? = null
)
