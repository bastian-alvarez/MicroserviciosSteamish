package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.entity.Category;
import com.gamestore.gamecatalog.repository.CategoryRepository;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Categorías", description = "API para gestión de categorías de juegos")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    
    @Operation(
        summary = "Listar todas las categorías", 
        description = "Obtiene la lista completa de categorías disponibles en el catálogo. Las categorías " +
                      "se utilizan para organizar y filtrar los juegos (ej: Acción, Aventura, RPG, etc.)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de categorías obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "204", 
            description = "No hay categorías registradas"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al obtener las categorías",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al obtener las categorías\"}"))
        )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Category>>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        
        List<EntityModel<Category>> categoryResources = categories.stream()
                .map(category -> {
                    EntityModel<Category> resource = EntityModel.of(category);
                    resource.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
                    resource.add(linkTo(methodOn(GameController.class).getAllGames(category.getId(), null, null, null)).withRel("games-by-category"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Category>> collection = CollectionModel.of(categoryResources);
        collection.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
        collection.add(linkTo(methodOn(GameController.class).getAllGames(null, null, null, null)).withRel("games"));
        collection.add(linkTo(methodOn(GenreController.class).getAllGenres()).withRel("genres"));
        
        return ResponseEntity.ok(collection);
    }
}

