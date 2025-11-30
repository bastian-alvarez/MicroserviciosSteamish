package com.gamestore.gamecatalog.config;

import com.gamestore.gamecatalog.entity.Category;
import com.gamestore.gamecatalog.entity.Game;
import com.gamestore.gamecatalog.entity.Genre;
import com.gamestore.gamecatalog.repository.CategoryRepository;
import com.gamestore.gamecatalog.repository.GameRepository;
import com.gamestore.gamecatalog.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Carga datos iniciales automáticamente la primera vez que se inicia la aplicación
 * Solo carga datos si la base de datos está vacía
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;
    private final GenreRepository genreRepository;
    
    @Override
    public void run(String... args) {
        // Verificar si ya hay juegos en la base de datos
        if (gameRepository.count() == 0) {
            log.info("📦 Base de datos vacía. Cargando datos precargados...");
            loadInitialData();
            log.info("✅ Datos precargados cargados exitosamente!");
        } else {
            log.info("ℹ️  La base de datos ya contiene {} juegos. No se cargarán datos iniciales.", gameRepository.count());
        }
    }
    
    private void loadInitialData() {
        // Asegurar que las categorías y géneros existan
        ensureCategoriesAndGenres();
        
        // Obtener referencias a categorías y géneros
        Category accionCat = categoryRepository.findByNombre("Acción").orElseThrow();
        Category aventuraCat = categoryRepository.findByNombre("Aventura").orElseThrow();
        Category rpgCat = categoryRepository.findByNombre("RPG").orElseThrow();
        Category deportesCat = categoryRepository.findByNombre("Deportes").orElseThrow();
        
        Genre accionGen = genreRepository.findByNombre("Acción").orElseThrow();
        Genre aventuraGen = genreRepository.findByNombre("Aventura").orElseThrow();
        Genre rpgGen = genreRepository.findByNombre("RPG").orElseThrow();
        Genre deportesGen = genreRepository.findByNombre("Deportes").orElseThrow();
        
        // Crear lista de juegos precargados
        List<Game> games = new ArrayList<>(Arrays.asList(
            new Game(null, "The Witcher 3: Wild Hunt", 
                "Un RPG de mundo abierto épico con una historia profunda y un combate dinámico. Explora un mundo fantástico lleno de monstruos y decisiones morales.", 
                Double.valueOf(29990.0), Integer.valueOf(50), "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500", 
                "CD Projekt RED", "2015", rpgCat.getId(), rpgGen.getId(), Boolean.TRUE, Integer.valueOf(0), null, null),
            
            new Game(null, "Cyberpunk 2077", 
                "Un RPG de acción en primera persona ambientado en Night City, una megalópolis obsesionada con el poder, el glamour y la modificación corporal.", 
                Double.valueOf(39990.0), Integer.valueOf(30), "https://images.unsplash.com/photo-1550745165-9bc20b82fabe?w=500", 
                "CD Projekt RED", "2020", rpgCat.getId(), accionGen.getId(), Boolean.TRUE, Integer.valueOf(25), null, null),
            
            new Game(null, "FIFA 24", 
                "El simulador de fútbol más realista con gráficos mejorados, física avanzada y modos de juego mejorados.", 
                Double.valueOf(49990.0), Integer.valueOf(100), "https://images.unsplash.com/photo-1551958219-acbc608c6377?w=500", 
                "EA Sports", "2023", deportesCat.getId(), deportesGen.getId(), Boolean.TRUE, Integer.valueOf(0), null, null),
            
            new Game(null, "Call of Duty: Modern Warfare", 
                "Un shooter en primera persona con una campaña intensa y multijugador competitivo.", 
                Double.valueOf(44990.0), Integer.valueOf(75), "https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=500", 
                "Infinity Ward", "2019", accionCat.getId(), accionGen.getId(), Boolean.TRUE, Integer.valueOf(15), null, null),
            
            new Game(null, "Assassin's Creed Valhalla", 
                "Embárcate en una aventura épica como Eivor, un guerrero vikingo en la Inglaterra del siglo IX.", 
                Double.valueOf(34990.0), Integer.valueOf(60), "https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=500", 
                "Ubisoft", "2020", aventuraCat.getId(), aventuraGen.getId(), Boolean.TRUE, Integer.valueOf(0), null, null),
            
            new Game(null, "Minecraft", 
                "El juego de construcción y aventura más popular del mundo. Crea, explora y sobrevive en mundos infinitos.", 
                Double.valueOf(19990.0), Integer.valueOf(200), "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500", 
                "Mojang Studios", "2011", aventuraCat.getId(), aventuraGen.getId(), Boolean.TRUE, Integer.valueOf(0), null, null),
            
            new Game(null, "Grand Theft Auto V", 
                "Vive la vida de tres criminales únicos en la ciudad de Los Santos en esta épica historia de crimen.", 
                Double.valueOf(24990.0), Integer.valueOf(80), "https://images.unsplash.com/photo-1550745165-9bc20b82fabe?w=500", 
                "Rockstar Games", "2013", accionCat.getId(), accionGen.getId(), Boolean.TRUE, Integer.valueOf(30), null, null),
            
            new Game(null, "The Legend of Zelda: Breath of the Wild", 
                "Explora el vasto reino de Hyrule en esta aventura de mundo abierto llena de misterios y desafíos.", 
                Double.valueOf(54990.0), Integer.valueOf(40), "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500", 
                "Nintendo", "2017", aventuraCat.getId(), aventuraGen.getId(), Boolean.TRUE, Integer.valueOf(0), null, null),
            
            new Game(null, "Counter-Strike 2", 
                "El shooter táctico competitivo definitivo con gráficos mejorados y mecánicas de juego refinadas.", 
                Double.valueOf(0.0), Integer.valueOf(999), "https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=500", 
                "Valve", "2023", accionCat.getId(), accionGen.getId(), Boolean.TRUE, Integer.valueOf(0), null, null),
            
            new Game(null, "Red Dead Redemption 2", 
                "Una épica historia del Oeste americano sobre honor y lealtad en los últimos días del Salvaje Oeste.", 
                Double.valueOf(39990.0), Integer.valueOf(55), "https://images.unsplash.com/photo-1550745165-9bc20b82fabe?w=500", 
                "Rockstar Games", "2018", aventuraCat.getId(), aventuraGen.getId(), Boolean.TRUE, Integer.valueOf(20), null, null),
            
            new Game(null, "Elden Ring", 
                "Un RPG de acción de mundo abierto ambientado en un mundo de fantasía oscuro creado por Hidetaka Miyazaki.", 
                Double.valueOf(49990.0), Integer.valueOf(35), "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500", 
                "FromSoftware", "2022", rpgCat.getId(), accionGen.getId(), Boolean.TRUE, Integer.valueOf(0), null, null),
            
            new Game(null, "God of War", 
                "Únete a Kratos y su hijo Atreus en un viaje épico a través de los reinos nórdicos.", 
                Double.valueOf(44990.0), Integer.valueOf(45), "https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=500", 
                "Santa Monica Studio", "2018", accionCat.getId(), aventuraGen.getId(), Boolean.TRUE, Integer.valueOf(10), null, null)
        ));
        
        // Guardar todos los juegos
        gameRepository.saveAll(games);
        log.info("✅ {} juegos precargados agregados a la base de datos", games.size());
    }
    
    private void ensureCategoriesAndGenres() {
        // Crear categorías si no existen
        if (categoryRepository.count() == 0) {
            List<Category> categories = Arrays.asList(
                new Category(null, "Acción", "Juegos de acción y aventura"),
                new Category(null, "Aventura", "Juegos de aventura y exploración"),
                new Category(null, "RPG", "Juegos de rol"),
                new Category(null, "Deportes", "Juegos deportivos"),
                new Category(null, "Estrategia", "Juegos de estrategia")
            );
            categoryRepository.saveAll(categories);
            log.info("✅ {} categorías creadas", categories.size());
        }
        
        // Crear géneros si no existen
        if (genreRepository.count() == 0) {
            List<Genre> genres = Arrays.asList(
                new Genre(null, "Acción", "Género de acción"),
                new Genre(null, "Aventura", "Género de aventura"),
                new Genre(null, "RPG", "Género de rol"),
                new Genre(null, "Deportes", "Género deportivo"),
                new Genre(null, "Estrategia", "Género de estrategia")
            );
            genreRepository.saveAll(genres);
            log.info("✅ {} géneros creados", genres.size());
        }
    }
}

