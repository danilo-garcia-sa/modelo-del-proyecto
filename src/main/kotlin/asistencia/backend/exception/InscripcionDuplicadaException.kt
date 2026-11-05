package asistencia.backend.exception

class InscripcionDuplicadaException(estudianteId: Long, cursoId: Long) :
    RuntimeException("El estudiante $estudianteId ya está inscripto en el curso $cursoId")
