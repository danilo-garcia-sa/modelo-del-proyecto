package asistencia.backend.modelo

import jakarta.persistence.*

@Entity
@Table(name = "estudiantes")
class Estudiante(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 100)
    var nombre: String = "",

    @Column(nullable = false, length = 100)
    var apellido: String = "",

    @Column(nullable = false, length = 20, unique = true)
    var dni: String = "",

    @Column(name = "foto_url", length = 255)
    var fotoUrl: String? = null // Para la foto carnet/DNI solicitada por el profesor
)
