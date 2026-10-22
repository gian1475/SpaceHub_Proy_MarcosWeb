-- Creamos las tablas y sus relaciones para MySQL 8.0.16 o posterior.
-- Las tablas existentes se conservan; este archivo no cambia su estructura.
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS spacehub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE spacehub_db;
 
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;
 
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  correo VARCHAR(100) NOT NULL UNIQUE,
  telefono VARCHAR(15),
  password VARCHAR(255) NOT NULL,
  rol_id BIGINT NOT NULL,
  CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;
 
CREATE TABLE IF NOT EXISTS categorias (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE,
  icono VARCHAR(50),
  descripcion VARCHAR(200)
) ENGINE=InnoDB;
 
CREATE TABLE IF NOT EXISTS servicios (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  categoria_id BIGINT NOT NULL,
  distrito VARCHAR(50) NOT NULL,
  direccion VARCHAR(150) NOT NULL,
  referencia VARCHAR(150),
  precio_por_hora DECIMAL(10,2) NOT NULL,
  capacidad INT NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
  descripcion_corta VARCHAR(200),
  descripcion TEXT,
  CONSTRAINT chk_servicio_precio CHECK (precio_por_hora >= 1.00),
  CONSTRAINT chk_servicio_capacidad CHECK (capacidad BETWEEN 1 AND 1000),
  CONSTRAINT chk_servicio_estado CHECK (estado IN ('Disponible', 'Ocupado')),
  CONSTRAINT fk_servicios_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;
 
CREATE TABLE IF NOT EXISTS servicio_incluidos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  servicio_id BIGINT NOT NULL,
  icono VARCHAR(50),
  texto VARCHAR(100) NOT NULL,
  CONSTRAINT fk_incluidos_servicio FOREIGN KEY (servicio_id) REFERENCES servicios(id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB;
 
CREATE TABLE IF NOT EXISTS servicio_imagenes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  servicio_id BIGINT NOT NULL,
  url VARCHAR(255) NOT NULL,
  CONSTRAINT fk_imagenes_servicio FOREIGN KEY (servicio_id) REFERENCES servicios(id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB;
 
CREATE TABLE IF NOT EXISTS reservas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  servicio_id BIGINT NOT NULL,
  usuario_id BIGINT NOT NULL,
  fecha DATE NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin TIME NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
  CONSTRAINT chk_reserva_horario CHECK (hora_fin > hora_inicio),
  CONSTRAINT chk_reserva_total CHECK (total >= 0),
  CONSTRAINT chk_reserva_estado CHECK (estado IN ('Pendiente', 'Aprobada', 'Rechazada')),
  INDEX idx_reserva_disponibilidad (servicio_id, fecha, estado, hora_inicio, hora_fin),
  CONSTRAINT fk_reservas_servicio FOREIGN KEY (servicio_id) REFERENCES servicios(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_reservas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;
