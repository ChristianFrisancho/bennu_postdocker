DROP TABLE if exists Paises CASCADE;
DROP TABLE if exists Sucursales CASCADE;
DROP TABLE if exists Trabajadores CASCADE;
DROP TABLE if exists Jornada CASCADE;
DROP TABLE if exists Horas_Extra CASCADE;

CREATE TABLE Paises (
    id_pais SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE Sucursales (
    id_sucursal SERIAL PRIMARY KEY,
    id_pais INT NOT NULL REFERENCES Paises(id_pais) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    hora VARCHAR(50) NOT NULL
);

CREATE TABLE Trabajadores (
    id_trabajador SERIAL PRIMARY KEY,
    id_sucursal INT NOT NULL REFERENCES Sucursales(id_sucursal),
    dni VARCHAR(20) UNIQUE NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    puesto VARCHAR(80) NOT NULL
);

CREATE TABLE Jornada (
    id_jornada SERIAL PRIMARY KEY,
    id_trabajador INT NOT NULL REFERENCES Trabajadores(id_trabajador) ON DELETE CASCADE,
    Fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    inicio_jornada TIMESTAMPTZ NOT NULL,
    termino_jornada TIMESTAMPTZ,
    inicio_descanso TIMESTAMPTZ,
    termino_descanso TIMESTAMPTZ,
    tardanza INT DEFAULT 0
);

CREATE TABLE Horas_Extra (
    id_horas_extra SERIAL PRIMARY KEY,
    id_jornada INT NOT NULL REFERENCES Jornada(id_jornada) ON DELETE CASCADE,
    inicio_extra TIMESTAMPTZ NOT NULL,
    fin_extra TIMESTAMPTZ NOT NULL,
    feriado BOOLEAN DEFAULT FALSE,
    noche BOOLEAN DEFAULT FALSE
);