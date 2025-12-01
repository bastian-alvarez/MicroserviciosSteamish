package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.entity.Genre;
import com.gamestore.gamecatalog.repository.GenreRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Géneros", description = "API para gestión de géneros de juegos")
public class GenreController {
    private final GenreRepository genreRepository;
    
    @Operation(
        summary = "Listar todos los géneros", 
        description = "Obtiene la lista completa de géneros disponibles en el catálogo. Los géneros " +
                      "se utilizan para clasificar y filtrar los juegos (ej: RPG, FPS, Estrategia, etc.)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de géneros obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "204", 
            description = "No hay géneros registrados"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al obtener los géneros",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al obtener los géneros\"}"))
        )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Genre>>> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        
        List<EntityModel<Genre>> genreResources = genres.stream()
                .map(genre -> {
                    EntityModel<Genre> resource = EntityModel.of(genre);
                    resource.add(linkTo(methodOn(GenreController.class).getAllGenres()).withSelfRel());
                    resource.add(linkTo(methodOn(GameController.class).getAllGames(null, genre.getId(), null, null)).withRel("games-by-genre"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Genre>> collection = CollectionModel.of(genreResources);
        collection.add(linkTo(methodOn(GenreController.class).getAllGenres()).withSelfRel());
        collection.add(linkTo(methodOn(GameController.class).getAllGames(null, null, null, null)).withRel("games"));
        collection.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        
        return ResponseEntity.ok(collection);
    }
}

