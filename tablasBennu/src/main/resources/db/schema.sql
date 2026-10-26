-- Esquema inicial para una base nueva. Conserva las tablas y los datos existentes.
-- Los nombres de columna se mantienen por compatibilidad con la base original.
CREATE TABLE IF NOT EXISTS paises (
    id_pais SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS sucursales (
    id_sucursal SERIAL PRIMARY KEY,
    id_pais INT NOT NULL REFERENCES paises(id_pais) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    hora VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS trabajadores (
    id_trabajador SERIAL PRIMARY KEY,
    id_sucursal INT NOT NULL REFERENCES sucursales(id_sucursal),
    dni VARCHAR(20) UNIQUE NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    puesto VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS jornada (
    id_jornada SERIAL PRIMARY KEY,
    id_trabajador INT NOT NULL REFERENCES trabajadores(id_trabajador) ON DELETE CASCADE,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    inicio_jornada TIMESTAMPTZ NOT NULL,
    termino_jornada TIMESTAMPTZ,
    inicio_descanso TIMESTAMPTZ,
    termino_descanso TIMESTAMPTZ,
    tardanza INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS horas_extra (
    id_horas_extra SERIAL PRIMARY KEY,
    id_jornada INT NOT NULL REFERENCES jornada(id_jornada) ON DELETE CASCADE,
    inicio_extra TIMESTAMPTZ NOT NULL,
    fin_extra TIMESTAMPTZ NOT NULL,
    feriado BOOLEAN DEFAULT FALSE,
    noche BOOLEAN DEFAULT FALSE
);
