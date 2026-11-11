package asistencia.backend.modelo

import jakarta.persistence.*
import java.time.LocalDate

/**
 * Una fecha del calendario de una cursada (una columna de la planilla).
 * Si sinActividad es true (feriado, clase suspendida...) no se toma asistencia ese día.
 */
@Entity
@Table(
    name = "dias_de_clase",
    uniqueConstraints = [UniqueConstraint(columnNames = ["cursada_id", "fecha"])]
)
class DiaDeClase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "cursada_id", nullable = false)
    var cursada: Cursada? = null,

    @Column(nullable = false)
    var fecha: LocalDate = LocalDate.now(),

    /** Número de clase dentro de la cursada (1, 2, 3...), contando también los días sin actividad. */
    @Column(nullable = false)
    var numero: Int = 0,

    @Column(name = "sin_actividad", nullable = false)
    var sinActividad: Boolean = false,

    @Column(length = 120)
    var motivo: String? = null
)
