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
