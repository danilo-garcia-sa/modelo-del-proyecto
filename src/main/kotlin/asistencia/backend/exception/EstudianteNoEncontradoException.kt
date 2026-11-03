package asistencia.backend.exception

class EstudianteNoEncontradoException(id: Long) :
    RuntimeException("Estudiante no encontrado con el ID: $id")
