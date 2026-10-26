package asistencia.backend.modelo

import jakarta.persistence.*

@Entity
@Table(name = "estudiantes")
data class Estudiante(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val nombre: String,
    val apellido: String,
    val dni: String,

    @Column(name = "foto_url")
    val fotoUrl: String? = null // Para la foto carnet/DNI solicitada por el profesor
)
