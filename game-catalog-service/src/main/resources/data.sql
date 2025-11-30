-- Datos precargados para Game Catalog Service
-- Este archivo se ejecuta automáticamente al iniciar la aplicación si la tabla está vacía
-- Solo inserta datos si no existen ya

-- Insertar juegos de ejemplo (solo si la tabla está vacía)
INSERT IGNORE INTO juegos (nombre, descripcion, precio, stock, imagen_url, desarrollador, fecha_lanzamiento, categoria_id, genero_id, activo, descuento) VALUES
('The Witcher 3: Wild Hunt', 'Un RPG de mundo abierto épico con una historia profunda y un combate dinámico. Explora un mundo fantástico lleno de monstruos y decisiones morales.', 29990, 50, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500', 'CD Projekt RED', '2015', 1, 1, TRUE, 0),
('Cyberpunk 2077', 'Un RPG de acción en primera persona ambientado en Night City, una megalópolis obsesionada con el poder, el glamour y la modificación corporal.', 39990, 30, 'https://images.unsplash.com/photo-1550745165-9bc20b82fabe?w=500', 'CD Projekt RED', '2020', 3, 1, TRUE, 25),
('FIFA 24', 'El simulador de fútbol más realista con gráficos mejorados, física avanzada y modos de juego mejorados.', 49990, 100, 'https://images.unsplash.com/photo-1551958219-acbc608c6377?w=500', 'EA Sports', '2023', 4, 4, TRUE, 0),
('Call of Duty: Modern Warfare', 'Un shooter en primera persona con una campaña intensa y multijugador competitivo.', 44990, 75, 'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=500', 'Infinity Ward', '2019', 1, 1, TRUE, 15),
('Assassin''s Creed Valhalla', 'Embárcate en una aventura épica como Eivor, un guerrero vikingo en la Inglaterra del siglo IX.', 34990, 60, 'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=500', 'Ubisoft', '2020', 2, 2, TRUE, 0),
('Minecraft', 'El juego de construcción y aventura más popular del mundo. Crea, explora y sobrevive en mundos infinitos.', 19990, 200, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500', 'Mojang Studios', '2011', 2, 2, TRUE, 0),
('Grand Theft Auto V', 'Vive la vida de tres criminales únicos en la ciudad de Los Santos en esta épica historia de crimen.', 24990, 80, 'https://images.unsplash.com/photo-1550745165-9bc20b82fabe?w=500', 'Rockstar Games', '2013', 1, 1, TRUE, 30),
('The Legend of Zelda: Breath of the Wild', 'Explora el vasto reino de Hyrule en esta aventura de mundo abierto llena de misterios y desafíos.', 54990, 40, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500', 'Nintendo', '2017', 2, 2, TRUE, 0),
('Counter-Strike 2', 'El shooter táctico competitivo definitivo con gráficos mejorados y mecánicas de juego refinadas.', 0, 999, 'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=500', 'Valve', '2023', 1, 1, TRUE, 0),
('Red Dead Redemption 2', 'Una épica historia del Oeste americano sobre honor y lealtad en los últimos días del Salvaje Oeste.', 39990, 55, 'https://images.unsplash.com/photo-1550745165-9bc20b82fabe?w=500', 'Rockstar Games', '2018', 2, 2, TRUE, 20),
('Elden Ring', 'Un RPG de acción de mundo abierto ambientado en un mundo de fantasía oscuro creado por Hidetaka Miyazaki.', 49990, 35, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500', 'FromSoftware', '2022', 3, 1, TRUE, 0),
('God of War', 'Únete a Kratos y su hijo Atreus en un viaje épico a través de los reinos nórdicos.', 44990, 45, 'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=500', 'Santa Monica Studio', '2018', 1, 2, TRUE, 10);

