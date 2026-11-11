-- Tablas nuevas para el calendario de clases (cursadas).
-- Correr UNA vez en el servidor de la escuela (perfil "lab", que solo valida el esquema).
-- En casa (perfil "local", ddl-auto=update) no hace falta: Hibernate las crea solo.

CREATE TABLE IF NOT EXISTS cursadas (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    docente_id   BIGINT       NOT NULL,
    curso_id     BIGINT       NOT NULL,
    dias         VARCHAR(30)  NOT NULL,
    fecha_inicio DATE         NOT NULL,
    fecha_fin    DATE         NOT NULL,
    CONSTRAINT fk_cursadas_docente FOREIGN KEY (docente_id) REFERENCES docentes (id),
    CONSTRAINT fk_cursadas_curso   FOREIGN KEY (curso_id)   REFERENCES cursos (id)
);

CREATE TABLE IF NOT EXISTS dias_de_clase (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    cursada_id    BIGINT      NOT NULL,
    fecha         DATE        NOT NULL,
    numero        INT         NOT NULL,
    sin_actividad BIT(1)      NOT NULL,
    motivo        VARCHAR(120) NULL,
    CONSTRAINT uk_dia_cursada_fecha UNIQUE (cursada_id, fecha),
    CONSTRAINT fk_dias_cursada FOREIGN KEY (cursada_id) REFERENCES cursadas (id)
);

ALTER TABLE asistencias ADD COLUMN cursada_id BIGINT NULL;
ALTER TABLE asistencias ADD CONSTRAINT fk_asistencias_cursada FOREIGN KEY (cursada_id) REFERENCES cursadas (id);

-- Opcional: las asistencias viejas (sin cursada) ya no se usan en la planilla.
-- DELETE FROM asistencias WHERE cursada_id IS NULL;
