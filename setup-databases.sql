-- Script para crear todas las bases de datos necesarias
-- Ejecutar en MySQL (phpMyAdmin o línea de comandos)

-- 1. Base de datos para Auth Service
CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile_photo_uri VARCHAR(500),
    is_blocked BOOLEAN DEFAULT FALSE,
    gender VARCHAR(50) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_blocked (is_blocked)
);

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('SUPER_ADMIN', 'GAME_MANAGER', 'SUPPORT', 'MODERATOR') NOT NULL,
    profile_photo_uri VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Insertar admin por defecto
INSERT INTO admins (name, email, phone, password, role) VALUES
('Administrador Principal', 'admin@steamish.com', '+56 9 8877 6655', '$2b$10$rQ8K8K8K8K8K8K8K8K8K8O8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8', 'SUPER_ADMIN'),
('Moderador', 'moderador@steamish.com', '+56 9 5544 3322', '$2b$10$rQ8K8K8K8K8K8K8K8K8K8O8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8', 'MODERATOR');

-- 2. Base de datos para Game Catalog Service
CREATE DATABASE IF NOT EXISTS games_db;
USE games_db;

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    INDEX idx_nombre (nombre)
);

CREATE TABLE IF NOT EXISTS generos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    INDEX idx_nombre (nombre)
);

CREATE TABLE IF NOT EXISTS juegos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    imagen_url VARCHAR(500),
    desarrollador VARCHAR(255) DEFAULT 'Desarrollador',
    fecha_lanzamiento VARCHAR(50) DEFAULT '2024',
    categoria_id BIGINT NOT NULL,
    genero_id BIGINT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    descuento INT DEFAULT 0,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE,
    FOREIGN KEY (genero_id) REFERENCES generos(id) ON DELETE CASCADE,
    INDEX idx_categoria (categoria_id),
    INDEX idx_genero (genero_id),
    INDEX idx_activo (activo),
    INDEX idx_descuento (descuento),
    INDEX idx_nombre (nombre)
);

-- Insertar categorías y géneros iniciales
INSERT INTO categorias (nombre, descripcion) VALUES
('Acción', 'Juegos de acción y aventura'),
('Aventura', 'Juegos de aventura y exploración'),
('RPG', 'Juegos de rol'),
('Deportes', 'Juegos deportivos'),
('Estrategia', 'Juegos de estrategia');

INSERT INTO generos (nombre, descripcion) VALUES
('Acción', 'Género de acción'),
('Aventura', 'Género de aventura'),
('RPG', 'Género de rol'),
('Deportes', 'Género deportivo'),
('Estrategia', 'Género de estrategia');

-- 3. Base de datos para Order Service
CREATE DATABASE IF NOT EXISTS orders_db;
USE orders_db;

CREATE TABLE IF NOT EXISTS estados (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS ordenes_compra (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    fecha_orden VARCHAR(50) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    estado_id BIGINT NOT NULL DEFAULT 1,
    metodo_pago VARCHAR(100) NOT NULL,
    direccion_envio VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (estado_id) REFERENCES estados(id),
    INDEX idx_user (user_id),
    INDEX idx_fecha (fecha_orden),
    INDEX idx_estado (estado_id)
);

CREATE TABLE IF NOT EXISTS detalles_orden (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    orden_id BIGINT NOT NULL,
    juego_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orden_id) REFERENCES ordenes_compra(id) ON DELETE CASCADE,
    INDEX idx_orden (orden_id),
    INDEX idx_juego (juego_id)
);

-- Insertar estados iniciales
INSERT INTO estados (nombre, descripcion) VALUES
('Pendiente', 'Orden pendiente de procesamiento'),
('Completada', 'Orden completada'),
('Cancelada', 'Orden cancelada');

-- 4. Base de datos para Library Service
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS biblioteca (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    juego_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    date_added VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'Disponible',
    genre VARCHAR(100) DEFAULT 'Acción',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_game (user_id, juego_id),
    INDEX idx_user (user_id),
    INDEX idx_juego (juego_id)
);

