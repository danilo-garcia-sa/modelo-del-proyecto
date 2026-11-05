package asistencia.backend.modelo

import jakarta.persistence.*

/**
 * Une a un estudiante con un curso. Un estudiante puede estar inscripto en varios cursos
 * y un curso tiene varios estudiantes, pero no se puede repetir el mismo par.
 */
@Entity
@Table(
    name = "inscripciones",
    uniqueConstraints = [UniqueConstraint(columnNames = ["estudiante_id", "curso_id"])]
)
class Inscripcion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    var estudiante: Estudiante? = null,

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    var curso: Curso? = null
)
