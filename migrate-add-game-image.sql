-- Script de migración para agregar el campo imagen_url a la tabla juegos
-- Ejecutar solo si la base de datos ya existía antes de esta actualización
-- Si creaste la base de datos con setup-databases.sql, NO necesitas ejecutar esto

USE games_db;

-- Verificar si la columna ya existe antes de agregarla
SET @dbname = DATABASE();
SET @tablename = 'juegos';
SET @columnname = 'imagen_url';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT "La columna imagen_url ya existe en la tabla juegos" AS resultado;',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(500) NULL AFTER precio;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Verificar resultado
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    CHARACTER_MAXIMUM_LENGTH, 
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'games_db'
  AND TABLE_NAME = 'juegos'
  AND COLUMN_NAME = 'imagen_url';

