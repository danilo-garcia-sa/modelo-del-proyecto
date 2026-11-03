package asistencia.backend.modelo

import jakarta.persistence.*

@Entity
@Table(name = "docentes")
class Docente(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 100)
    var nombre: String = "",

    @Column(nullable = false, length = 100)
    var apellido: String = "",

    @Column(nullable = false, length = 150, unique = true)
    var email: String = ""
)
