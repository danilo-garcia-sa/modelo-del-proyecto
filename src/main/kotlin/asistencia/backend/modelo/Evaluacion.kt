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
    var curso: Curso? = null,

    // Cursada (docente + curso) a la que pertenece; es null en las evaluaciones viejas.
    @ManyToOne
    @JoinColumn(name = "cursada_id")
    var cursada: Cursada? = null,

    // Puntos que vale la evaluación en el sistema de puntos (ej. 25).
    @Column(name = "puntos_maximos")
    var puntosMaximos: Int? = null
)
