package asistencia.backend.modelo

import jakarta.persistence.*
import java.time.LocalDate

/**
 * Una "cursada" es un docente dando un curso en ciertos días de la semana durante un período
 * (el año lectivo o un cuatrimestre). De acá sale el calendario de clases y la planilla de asistencia.
 *
 * Los días de la semana se guardan como texto con el número ISO de cada día
 * (1 = lunes ... 7 = domingo), separados por coma. Ej: "1" = lunes, "1,3" = lunes y miércoles.
 */
@Entity
@Table(name = "cursadas")
class Cursada(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    var docente: Docente? = null,

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    var curso: Curso? = null,

    @Column(name = "dias", nullable = false, length = 30)
    var dias: String = "",

    @Column(name = "fecha_inicio", nullable = false)
    var fechaInicio: LocalDate = LocalDate.now(),

    @Column(name = "fecha_fin", nullable = false)
    var fechaFin: LocalDate = LocalDate.now(),

    /** "NOTAS" (instancias con nota 1 a 10) o "PUNTOS" (se suman puntos de TPs; ej. el sistema de Joaquín). */
    @Column(name = "sistema", nullable = false, length = 10, columnDefinition = "varchar(10) not null default 'NOTAS'")
    var sistema: String = "NOTAS",

    /** Puntos mínimos para aprobar en el sistema de puntos. Si es null se usa 144 (6 pts × 24 clases). */
    @Column(name = "puntos_aprobar")
    var puntosAprobar: Int? = null
)
