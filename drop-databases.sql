-- ============================================================================
-- Script para eliminar todas las bases de datos del sistema
-- ⚠️ ADVERTENCIA: Este script eliminará TODAS las bases de datos y sus datos
-- Ejecutar con precaución. No se puede deshacer.
-- ============================================================================

-- ============================================================================
-- 1. ELIMINAR BASE DE DATOS AUTH SERVICE
-- ============================================================================
DROP DATABASE IF EXISTS auth_db;

-- ============================================================================
-- 2. ELIMINAR BASE DE DATOS GAME CATALOG SERVICE
-- ============================================================================
DROP DATABASE IF EXISTS games_db;

-- ============================================================================
-- 3. ELIMINAR BASE DE DATOS ORDER SERVICE
-- ============================================================================
DROP DATABASE IF EXISTS orders_db;

-- ============================================================================
-- 4. ELIMINAR BASE DE DATOS LIBRARY SERVICE
-- ============================================================================
DROP DATABASE IF EXISTS library_db;

-- ============================================================================
-- VERIFICACIÓN FINAL
-- ============================================================================
SELECT '✅ Todas las bases de datos han sido eliminadas exitosamente' AS resultado;

-- Nota: Si quieres eliminar solo las tablas pero mantener las bases de datos,
-- descomenta las siguientes secciones:

-- ============================================================================
-- OPCIÓN ALTERNATIVA: Eliminar solo las tablas (mantener las bases de datos)
-- ============================================================================

/*
-- Eliminar tablas de auth_db
USE auth_db;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS users;

-- Eliminar tablas de games_db
USE games_db;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS comentarios;
DROP TABLE IF EXISTS juegos;
DROP TABLE IF EXISTS generos;
DROP TABLE IF EXISTS categorias;

-- Eliminar tablas de orders_db
USE orders_db;
DROP TABLE IF EXISTS detalles_orden;
DROP TABLE IF EXISTS ordenes_compra;
DROP TABLE IF EXISTS estados;

-- Eliminar tablas de library_db
USE library_db;
DROP TABLE IF EXISTS biblioteca;
*/

