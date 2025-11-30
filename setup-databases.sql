-- ============================================================================
-- Script completo y final para crear todas las bases de datos del sistema
-- Incluye: Auth (con notificaciones), Games (con comentarios y ratings), Orders, Library
-- Ejecutar en MySQL (phpMyAdmin o línea de comandos)
-- ============================================================================

-- ============================================================================
-- 1. BASE DE DATOS PARA AUTH SERVICE
-- ============================================================================
CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

-- Tabla de usuarios
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

-- Tabla de administradores (incluye moderadores)
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

-- Tabla de notificaciones
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message VARCHAR(500) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insertar admin y moderador por defecto
-- NOTA: Las contraseñas son hasheadas con BCrypt. En producción, cambiar estas contraseñas.
-- Contraseña para admin: Admin123!
-- Contraseña para moderador: password123 (cambiar si es necesario)
INSERT INTO admins (name, email, phone, password, role) VALUES
('Administrador Principal', 'admin@steamish.com', '+56 9 8877 6655', '$2a$10$B5lFvoBPiBNqrddckeOyFOKm2VeYS./eNc8DI06sA05S0RrLfWOQO', 'SUPER_ADMIN'),
('Moderador', 'moderador@steamish.com', '+56 9 5544 3322', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MODERATOR')
ON DUPLICATE KEY UPDATE name=name;

-- Insertar usuario de prueba
-- NOTA: Contraseña hasheada con BCrypt. Contraseña: "password123"
INSERT INTO users (name, email, phone, password, is_blocked, gender) VALUES
('Usuario Prueba', 'usuario@test.com', '+56 9 1234 5678', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', FALSE, 'Masculino')
ON DUPLICATE KEY UPDATE name=name;

-- ============================================================================
-- 2. BASE DE DATOS PARA GAME CATALOG SERVICE
-- ============================================================================
CREATE DATABASE IF NOT EXISTS games_db;
USE games_db;

-- Tabla de categorías
CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    INDEX idx_nombre (nombre)
);

-- Tabla de géneros
CREATE TABLE IF NOT EXISTS generos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    INDEX idx_nombre (nombre)
);

-- Tabla de juegos
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

-- Tabla de comentarios
CREATE TABLE IF NOT EXISTS comentarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    juego_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    usuario_nombre VARCHAR(255) NOT NULL,
    contenido TEXT NOT NULL,
    is_hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (juego_id) REFERENCES juegos(id) ON DELETE CASCADE,
    INDEX idx_juego (juego_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_hidden (is_hidden),
    INDEX idx_created (created_at)
);

-- Tabla de calificaciones/ratings
CREATE TABLE IF NOT EXISTS ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    juego_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    calificacion INT NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (juego_id) REFERENCES juegos(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_game_rating (juego_id, usuario_id),
    INDEX idx_juego (juego_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_calificacion (calificacion)
);

-- Insertar categorías iniciales
INSERT INTO categorias (nombre, descripcion) VALUES
('Acción', 'Juegos de acción y aventura'),
('Aventura', 'Juegos de aventura y exploración'),
('RPG', 'Juegos de rol'),
('Deportes', 'Juegos deportivos'),
('Estrategia', 'Juegos de estrategia')
ON DUPLICATE KEY UPDATE nombre=nombre;

-- Insertar géneros iniciales
INSERT INTO generos (nombre, descripcion) VALUES
('Acción', 'Género de acción'),
('Aventura', 'Género de aventura'),
('RPG', 'Género de rol'),
('Deportes', 'Género deportivo'),
('Estrategia', 'Género de estrategia')
ON DUPLICATE KEY UPDATE nombre=nombre;

-- Insertar juegos de prueba (10 juegos)
INSERT INTO juegos (nombre, descripcion, precio, stock, imagen_url, desarrollador, fecha_lanzamiento, categoria_id, genero_id, activo, descuento) VALUES
('The Witcher 3: Wild Hunt', 'Un RPG de mundo abierto épico donde juegas como Geralt de Rivia, un cazador de monstruos. Explora un vasto mundo lleno de misiones, criaturas y decisiones que afectan la historia.', 29.99, 50, NULL, 'CD Projekt RED', '2015', 3, 3, TRUE, 0),
('Grand Theft Auto V', 'El juego de mundo abierto más vendido. Vive la vida de tres criminales diferentes en la ciudad de Los Santos mientras realizan misiones y exploran un mundo lleno de posibilidades.', 39.99, 100, NULL, 'Rockstar Games', '2013', 1, 1, TRUE, 15),
('FIFA 24', 'El simulador de fútbol más realista. Juega con tus equipos favoritos, compite en ligas y torneos, y vive la emoción del fútbol en la consola.', 59.99, 200, NULL, 'EA Sports', '2023', 4, 4, TRUE, 0),
('Minecraft', 'El juego de construcción y supervivencia más popular del mundo. Crea, explora y sobrevive en un mundo infinito hecho de bloques.', 19.99, 500, NULL, 'Mojang Studios', '2011', 2, 2, TRUE, 10),
('Counter-Strike 2', 'El shooter táctico competitivo definitivo. Únete a equipos de terroristas o contra-terroristas en partidas intensas de 5v5.', 0.00, 0, NULL, 'Valve', '2023', 1, 1, TRUE, 0),
('Red Dead Redemption 2', 'Una épica historia del Salvaje Oeste. Únete a la banda de Van der Linde mientras huyen de la ley en una aventura llena de acción y drama.', 49.99, 75, NULL, 'Rockstar Games', '2018', 2, 1, TRUE, 20),
('The Elder Scrolls V: Skyrim', 'El RPG de fantasía más aclamado. Explora el reino de Skyrim, completa misiones épicas y personaliza tu personaje en un mundo lleno de dragones y magia.', 24.99, 150, NULL, 'Bethesda Game Studios', '2011', 3, 3, TRUE, 0),
('Call of Duty: Modern Warfare III', 'La experiencia de combate más intensa. Únete a la guerra moderna con gráficos realistas y multijugador competitivo.', 69.99, 300, NULL, 'Infinity Ward', '2023', 1, 1, TRUE, 0),
('Civilization VI', 'Construye un imperio que perdure a través de los siglos. Lidera tu civilización desde la antigüedad hasta la era moderna en este juego de estrategia por turnos.', 34.99, 80, NULL, 'Firaxis Games', '2016', 5, 5, TRUE, 25),
('Assassin''s Creed Valhalla', 'Conviértete en Eivor, un feroz guerrero vikingo. Saquea Inglaterra, construye tu asentamiento y forja tu leyenda en la era vikinga.', 44.99, 120, NULL, 'Ubisoft Montreal', '2020', 2, 1, TRUE, 0)
ON DUPLICATE KEY UPDATE nombre=nombre;

-- ============================================================================
-- 3. BASE DE DATOS PARA ORDER SERVICE
-- ============================================================================
CREATE DATABASE IF NOT EXISTS orders_db;
USE orders_db;

-- Tabla de estados de orden
CREATE TABLE IF NOT EXISTS estados (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    INDEX idx_nombre (nombre)
);

-- Insertar estados iniciales
INSERT INTO estados (nombre, descripcion) VALUES
('Pendiente', 'Orden pendiente de procesamiento'),
('Procesando', 'Orden en proceso'),
('Completada', 'Orden completada'),
('Cancelada', 'Orden cancelada')
ON DUPLICATE KEY UPDATE nombre=nombre;

-- Tabla de órdenes
CREATE TABLE IF NOT EXISTS ordenes_compra (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    fecha_orden VARCHAR(255) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    estado_id BIGINT NOT NULL DEFAULT 1,
    metodo_pago VARCHAR(100) NOT NULL DEFAULT 'Tarjeta',
    direccion_envio TEXT,
    FOREIGN KEY (estado_id) REFERENCES estados(id),
    INDEX idx_user (user_id),
    INDEX idx_estado (estado_id),
    INDEX idx_fecha (fecha_orden)
);

-- Tabla de detalles de orden
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

-- ============================================================================
-- 4. BASE DE DATOS PARA LIBRARY SERVICE
-- ============================================================================
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Tabla de biblioteca
CREATE TABLE IF NOT EXISTS biblioteca (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    juego_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    genre VARCHAR(100),
    status VARCHAR(50) DEFAULT 'Disponible',
    date_added VARCHAR(255) NOT NULL,
    INDEX idx_user (user_id),
    INDEX idx_juego (juego_id),
    INDEX idx_status (status),
    UNIQUE KEY unique_user_game (user_id, juego_id)
);

-- ============================================================================
-- VERIFICACIÓN FINAL
-- ============================================================================
SELECT 'Base de datos auth_db creada' AS mensaje;
SELECT COUNT(*) AS total_usuarios FROM auth_db.users;
SELECT COUNT(*) AS total_admins FROM auth_db.admins;
SELECT COUNT(*) AS total_notificaciones FROM auth_db.notifications;

SELECT 'Base de datos games_db creada' AS mensaje;
SELECT COUNT(*) AS total_categorias FROM games_db.categorias;
SELECT COUNT(*) AS total_generos FROM games_db.generos;
SELECT COUNT(*) AS total_juegos FROM games_db.juegos;
SELECT COUNT(*) AS total_comentarios FROM games_db.comentarios;
SELECT COUNT(*) AS total_ratings FROM games_db.ratings;

SELECT 'Base de datos orders_db creada' AS mensaje;
SELECT COUNT(*) AS total_estados FROM orders_db.estados;
SELECT COUNT(*) AS total_ordenes FROM orders_db.ordenes_compra;

SELECT 'Base de datos library_db creada' AS mensaje;
SELECT COUNT(*) AS total_biblioteca FROM library_db.biblioteca;

SELECT '✅ Todas las bases de datos han sido creadas exitosamente' AS resultado;
