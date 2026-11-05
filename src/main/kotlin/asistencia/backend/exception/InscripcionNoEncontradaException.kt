package asistencia.backend.exception

class InscripcionNoEncontradaException(estudianteId: Long, cursoId: Long) :
    RuntimeException("El estudiante $estudianteId no está inscripto en el curso $cursoId")
