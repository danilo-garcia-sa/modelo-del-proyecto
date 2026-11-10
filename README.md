# Anotame! — backend

API en Kotlin + Spring Boot + MariaDB para registrar asistencia y calcular calificaciones.

## Cómo correrlo

El proyecto tiene dos perfiles de base de datos:

| Perfil | Dónde | Base | Esquema |
|--------|-------|------|---------|
| `lab` (por defecto) | Escuela | Servidor de la escuela (`Anotame-UBA`) | Ya existe (`ddl-auto=validate`) |
| `local` | Casa | MariaDB local (`etec_anotame`) | Lo crea Hibernate (`ddl-auto=update`) |

### En la escuela (PowerShell)

```powershell
$env:DB_PASSWORD="la-clave-del-servidor"
.\mvnw spring-boot:run
```

### En casa (PowerShell)

1. Creá la base vacía en tu MariaDB: `CREATE DATABASE etec_anotame;`
2. Corré:

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
$env:DB_USER="root"            # opcional, por defecto root
$env:DB_PASSWORD="tu-clave"    # opcional, por defecto vacía
.\mvnw spring-boot:run
```

El servidor queda en `http://localhost:8080` y las rutas empiezan con `/api/`
(`/api/estudiantes`, `/api/cursos`, `/api/docentes`, `/api/asistencias`, `/api/evaluaciones`, `/api/notas`).

> Las contraseñas no se guardan en el repo: se pasan siempre por variables de entorno.

> **Servidor de la escuela:** la tabla `inscripciones` hay que crearla una vez con
> [`docs/inscripciones.sql`](docs/inscripciones.sql), porque ese perfil solo valida el esquema.

## Datos de prueba

Al arrancar, el backend puede cargar datos de prueba (docentes, 5to A/B/C con 5 alumnos inscriptos
cada uno, y para 5to A tres evaluaciones, notas y cinco días de asistencia de los tres primeros alumnos).
Todos los nombres y DNI son inventados.

- **Casa (perfil `local`)**: viene activado.
- **Escuela (perfil `lab`)**: apagado por defecto. Para encenderlo: `$env:ANOTAME_SEED_ENABLED="true"`.
- Docentes, cursos, evaluaciones, notas y asistencias se cargan **solo si su tabla está vacía**.
  Estudiantes e inscripciones se completan por DNI (se agrega lo que falte). Reiniciar no duplica nada.
- El código está en `config/DatosDePrueba.kt`.

## Inscripciones (alumnos de cada curso)

| Método | Ruta | Qué hace |
|--------|------|----------|
| GET | `/api/cursos/{cursoId}/estudiantes` | Lista los alumnos del curso (ordenados por apellido) |
| POST | `/api/cursos/{cursoId}/estudiantes/{estudianteId}` | Inscribe al alumno en el curso (409 si ya estaba) |
| DELETE | `/api/cursos/{cursoId}/estudiantes/{estudianteId}` | Saca al alumno del curso |

## Filtros en los listados (todos opcionales)

- `GET /api/asistencias?cursoId=1&fecha=2026-11-05&estudianteId=3`
- `GET /api/evaluaciones?cursoId=1`
- `GET /api/notas?cursoId=1&evaluacionId=2&estudianteId=3`
