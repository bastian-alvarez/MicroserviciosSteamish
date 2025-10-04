package com.juegos.juegos.controller;


import com.juegos.juegos.model.Juego;
import com.juegos.juegos.service.JuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Juegos", description = "CRUD del microservicio de Juegos")
@RestController
@RequestMapping("/api/juegos")
public class JuegoController {

  private final JuegoService service;
  private final JuegoAssembler assembler = new JuegoAssembler();

  public JuegoController(JuegoService service) { this.service = service; }

  @Operation(summary = "Listar juegos (paginado y filtros)")
  @GetMapping
  public CollectionModel<EntityModel<Juego>> list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String categoriaId,
      @RequestParam(required = false) String generoId,
      @RequestParam(required = false) String estadoId,
      @PageableDefault(size = 20, sort = "nombreJuego") Pageable pageable) {

    Page<Juego> page = service.listar(q, categoriaId, generoId, estadoId, pageable);
    java.util.List<EntityModel<Juego>> models = page.getContent().stream().map(assembler::toModel).toList();

    return CollectionModel.of(models,
      linkTo(methodOn(JuegoController.class).list(q, categoriaId, generoId, estadoId, pageable)).withSelfRel(),
      linkTo(methodOn(JuegoController.class).create(null)).withRel("create"));
  }

  @Operation(summary = "Obtener juego por id")
  @GetMapping("/{id}")
  public EntityModel<Juego> get(@PathVariable String id) {
    return assembler.toModel(service.obtener(id));
  }

  @Operation(summary = "Crear juego")
  @PostMapping
  public ResponseEntity<EntityModel<Juego>> create(@Valid @RequestBody Juego body) {
    Juego saved = service.crear(body);
    return ResponseEntity
      .created(linkTo(methodOn(JuegoController.class).get(saved.getId())).toUri())
      .body(assembler.toModel(saved));
  }

  @Operation(summary = "Actualizar juego")
  @PutMapping("/{id}")
  public EntityModel<Juego> update(@PathVariable String id, @Valid @RequestBody Juego body) {
    return assembler.toModel(service.actualizar(id, body));
  }

  @Operation(summary = "Eliminar juego")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  // Errores simples
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(java.util.Map.of("error","BAD_REQUEST","message",ex.getMessage()));
  }

  // ---- HATEOAS assembler ----
  static class JuegoAssembler implements RepresentationModelAssembler<Juego, EntityModel<Juego>> {
    @Override public EntityModel<Juego> toModel(Juego j) {
      return EntityModel.of(j,
        linkTo(methodOn(JuegoController.class).get(j.getId())).withSelfRel(),
        linkTo(methodOn(JuegoController.class).list(null,null,null,null, PageRequest.of(0,20))).withRel("collection"));
    }
  }
}

