-- Tabla nueva "inscripciones" (estudiante <-> curso).
--
-- En casa (perfil "local", ddl-auto=update) Hibernate la crea sola.
-- En el servidor de la escuela (perfil "lab", ddl-auto=validate) NO se crea sola:
-- hay que correr este script UNA vez en la base "Anotame-UBA" antes de levantar el backend,
-- si no el arranque falla diciendo que falta la tabla [inscripciones].

CREATE TABLE IF NOT EXISTS inscripciones (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    estudiante_id BIGINT NOT NULL,
    curso_id      BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inscripcion_estudiante_curso (estudiante_id, curso_id),
    CONSTRAINT fk_inscripcion_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes (id),
    CONSTRAINT fk_inscripcion_curso      FOREIGN KEY (curso_id)      REFERENCES cursos (id)
) ENGINE=InnoDB;
