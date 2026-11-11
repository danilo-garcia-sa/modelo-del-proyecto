package asistencia.backend.modelo

import jakarta.persistence.*

/**
 * Resultado de un alumno en una instancia de la cursada:
 * BIMESTRE_1, CUATRIMESTRE_1, BIMESTRE_2 o CUATRIMESTRE_2.
 * - Bimestres: se guarda un estado (APROBADO, EN_PROCESO, DESAPROBADO).
 * - Cuatrimestres: se guarda una nota entera de 1 a 10 (se aprueba con 7 o más).
 */
@Entity
@Table(
    name = "calificaciones",
    uniqueConstraints = [UniqueConstraint(columnNames = ["cursada_id", "estudiante_id", "instancia"])]
)
class Calificacion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "cursada_id", nullable = false)
    var cursada: Cursada? = null,

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    var estudiante: Estudiante? = null,

    @Column(nullable = false, length = 20)
    var instancia: String = "",

    @Column
    var nota: Int? = null,

    @Column(length = 15)
    var estado: String? = null
)
