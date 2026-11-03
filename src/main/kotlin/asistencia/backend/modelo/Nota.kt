package asistencia.backend.modelo

import jakarta.persistence.*

@Entity
@Table(name = "notas")
class Nota(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var valor: Double = 0.0,

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    var estudiante: Estudiante? = null,

    @ManyToOne
    @JoinColumn(name = "evaluacion_id", nullable = false)
    var evaluacion: Evaluacion? = null
)
