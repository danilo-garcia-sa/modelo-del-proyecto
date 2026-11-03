package asistencia.backend.modelo

import jakarta.persistence.*

@Entity
@Table(name = "cursos")
class Curso(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 120)
    var nombre: String = "", // Ej: "3º Año A - Matemática"

    @Column(name = "anio_lectivo", nullable = false)
    var anioLectivo: Int = 0,

    @ManyToOne
    @JoinColumn(name = "docente_id")
    var docente: Docente? = null
)
