package com.resena.resena.controller;


import com.resena.resena.model.Resena;
import com.resena.resena.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Reseñas", description = "CRUD y moderación de reseñas")
@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

  private final ResenaService service;
  private final ResenaAssembler assembler = new ResenaAssembler();

  public ResenaController(ResenaService service) { this.service = service; }

  // ---- LISTAR ----
  @Operation(summary = "Listar reseñas (paginado y filtros)")
  @GetMapping
  public CollectionModel<EntityModel<Resena>> list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String juegoId,
      @RequestParam(required = false) String usuarioId,
      @RequestParam(required = false) String estadoId,
      @PageableDefault(size = 20, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<Resena> page = service.listar(q, juegoId, usuarioId, estadoId, pageable);
    var models = page.getContent().stream().map(assembler::toModel).toList();
    return CollectionModel.of(models,
      linkTo(methodOn(ResenaController.class).list(q, juegoId, usuarioId, estadoId, pageable)).withSelfRel(),
      linkTo(methodOn(ResenaController.class).create(null)).withRel("create"));
  }

  // ---- OBTENER ----
  @Operation(summary = "Obtener reseña por id")
  @GetMapping("/{id}")
  public EntityModel<Resena> get(@PathVariable String id) {
    return assembler.toModel(service.obtener(id));
  }

  // ---- CREAR ----
  @Operation(summary = "Crear reseña")
  @PostMapping
  public ResponseEntity<EntityModel<Resena>> create(@Valid @RequestBody Resena body) {
    var saved = service.crear(body);
    return ResponseEntity
      .created(linkTo(methodOn(ResenaController.class).get(saved.getId())).toUri())
      .body(assembler.toModel(saved));
  }

  // ---- ACTUALIZAR ----
  @Operation(summary = "Actualizar reseña (calificación/comentario/estado)")
  @PutMapping("/{id}")
  public EntityModel<Resena> update(@PathVariable String id, @Valid @RequestBody Resena body) {
    return assembler.toModel(service.actualizar(id, body));
  }

  // ---- ELIMINAR ----
  @Operation(summary = "Eliminar reseña")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  // ---- MODERACIÓN: CAMBIAR ESTADO ----
  @Operation(summary = "Cambiar estado de la reseña (APROBADA/RECHAZADA/BANEADA/...)")
  @PostMapping("/{id}/estado")
  public EntityModel<Resena> cambiarEstado(@PathVariable String id, @RequestParam String estadoId) {
    return assembler.toModel(service.cambiarEstado(id, estadoId));
  }

  // ---- MÉTRICA: PROMEDIO POR JUEGO ----
  @Operation(summary = "Promedio de calificación por juego")
  @GetMapping("/juegos/{juegoId}/promedio")
  public ResponseEntity<java.util.Map<String, Object>> promedio(
      @PathVariable String juegoId,
      @RequestParam(defaultValue = "true") boolean soloAprobadas) {

    BigDecimal avg = service.promedioPorJuego(juegoId, soloAprobadas);
    return ResponseEntity.ok(java.util.Map.of(
      "juegoId", juegoId,
      "soloAprobadas", soloAprobadas,
      "promedio", avg
    ));
  }

  // ---- Errores simples ----
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(java.util.Map.of("error","BAD_REQUEST","message",ex.getMessage()));
  }

  // ---- HATEOAS Assembler ----
  static class ResenaAssembler implements RepresentationModelAssembler<Resena, EntityModel<Resena>> {
    @Override public EntityModel<Resena> toModel(Resena r) {
      return EntityModel.of(r,
        linkTo(methodOn(ResenaController.class).get(r.getId())).withSelfRel(),
        linkTo(methodOn(ResenaController.class).list(null, r.getJuegoId(), null, "APROBADA",
                PageRequest.of(0,20))).withRel("aprobadas_del_juego"),
        linkTo(methodOn(ResenaController.class).list(null, r.getJuegoId(), null, null,
                PageRequest.of(0,20))).withRel("todas_del_juego"),
        linkTo(methodOn(ResenaController.class).list(null, null, r.getUsuarioId(), null,
                PageRequest.of(0,20))).withRel("del_usuario"),
        linkTo(methodOn(ResenaController.class).list(null, null, null, null,
                PageRequest.of(0,20))).withRel("collection")
      );
    }
  }
}

