package com.orden.orden.controller;


import com.orden.orden.dto.OrdenResumenDto;
import com.orden.orden.model.Detalle;
import com.orden.orden.model.OrdenCompra;
import com.orden.orden.service.OrdenService;
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

@Tag(name = "Órdenes", description = "OrdenCompra y Detalles")
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

  private final OrdenService service;
  private final OrdenAssembler ordenAsm = new OrdenAssembler();
  private final DetalleAssembler detAsm = new DetalleAssembler();

  public OrdenController(OrdenService service) { this.service = service; }

  // ---- ÓRDENES ----
  @Operation(summary = "Listar órdenes (paginado y filtros)")
  @GetMapping
  public CollectionModel<EntityModel<OrdenCompra>> list(
      @RequestParam(required = false) String usuarioId,
      @RequestParam(required = false) String estadoId,
      @PageableDefault(size = 20, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<OrdenCompra> page = service.listar(usuarioId, estadoId, pageable);
    var models = page.getContent().stream().map(ordenAsm::toModel).toList();

    return CollectionModel.of(models,
      linkTo(methodOn(OrdenController.class).list(usuarioId, estadoId, pageable)).withSelfRel(),
      linkTo(methodOn(OrdenController.class).create(null)).withRel("create"));
  }

  @Operation(summary = "Obtener orden por id")
  @GetMapping("/{id}")
  public EntityModel<OrdenCompra> get(@PathVariable String id) {
    return ordenAsm.toModel(service.obtener(id));
  }

  @Operation(summary = "Crear orden (borrador)")
  @PostMapping
  public ResponseEntity<EntityModel<OrdenCompra>> create(@Valid @RequestBody OrdenCompra body) {
    var saved = service.crear(body);
    return ResponseEntity
      .created(linkTo(methodOn(OrdenController.class).get(saved.getId())).toUri())
      .body(ordenAsm.toModel(saved));
  }

  @Operation(summary = "Actualizar orden (usuario/estado)")
  @PutMapping("/{id}")
  public EntityModel<OrdenCompra> update(@PathVariable String id, @Valid @RequestBody OrdenCompra body) {
    return ordenAsm.toModel(service.actualizar(id, body));
  }

  @Operation(summary = "Eliminar orden y sus detalles")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Confirmar orden (recalcula total)")
  @PostMapping("/{id}/confirmar")
  public EntityModel<OrdenCompra> confirmar(@PathVariable String id) {
    return ordenAsm.toModel(service.confirmar(id));
  }

  @Operation(summary = "Resumen enriquecido de la orden (usuario, juegos, licencias, reseñas)")
  @GetMapping("/{id}/resumen")
  public OrdenResumenDto resumen(@PathVariable String id) {
    return service.construirResumen(id);
  }

  // ---- DETALLES ----
  @Operation(summary = "Listar detalles de una orden")
  @GetMapping("/{id}/detalles")
  public CollectionModel<EntityModel<Detalle>> listDetalles(@PathVariable String id,
      @PageableDefault(size = 50) Pageable pageable) {
    var page = service.listarDetalles(id, pageable);
    var models = page.getContent().stream().map(detAsm::toModel).toList();
    return CollectionModel.of(models,
      linkTo(methodOn(OrdenController.class).listDetalles(id, pageable)).withSelfRel(),
      linkTo(methodOn(OrdenController.class).addDetalle(id, null)).withRel("add-item"));
  }

  @Operation(summary = "Agregar detalle a una orden")
  @PostMapping("/{id}/detalles")
  public ResponseEntity<EntityModel<Detalle>> addDetalle(@PathVariable String id, @Valid @RequestBody Detalle body) {
    var saved = service.agregarDetalle(id, body);
    return ResponseEntity
      .created(linkTo(methodOn(OrdenController.class).getDetalle(id, saved.getId())).toUri())
      .body(detAsm.toModel(saved));
  }

  @Operation(summary = "Obtener un detalle de la orden")
  @GetMapping("/{id}/detalles/{detalleId}")
  public EntityModel<Detalle> getDetalle(@PathVariable String id, @PathVariable String detalleId) {
    var det = service.obtenerDetalle(detalleId);
    if (!id.equals(det.getOrdenId())) throw new IllegalArgumentException("Detalle no pertenece a la orden");
    return detAsm.toModel(det);
  }

  @Operation(summary = "Actualizar un detalle de la orden")
  @PutMapping("/{id}/detalles/{detalleId}")
  public EntityModel<Detalle> updDetalle(@PathVariable String id, @PathVariable String detalleId,
                                         @Valid @RequestBody Detalle body) {
    return detAsm.toModel(service.actualizarDetalle(id, detalleId, body));
  }

  @Operation(summary = "Eliminar detalle de la orden")
  @DeleteMapping("/{id}/detalles/{detalleId}")
  public ResponseEntity<Void> delDetalle(@PathVariable String id, @PathVariable String detalleId) {
    service.eliminarDetalle(id, detalleId);
    return ResponseEntity.noContent().build();
  }

  // ---- Error simple ----
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(java.util.Map.of("error","BAD_REQUEST","message",ex.getMessage()));
  }

  // ---- HATEOAS assemblers ----
  static class OrdenAssembler implements RepresentationModelAssembler<OrdenCompra, EntityModel<OrdenCompra>> {
    @Override public EntityModel<OrdenCompra> toModel(OrdenCompra o) {
      return EntityModel.of(o,
        linkTo(methodOn(OrdenController.class).get(o.getId())).withSelfRel(),
        linkTo(methodOn(OrdenController.class).list(null,null, PageRequest.of(0,20))).withRel("collection"),
        linkTo(methodOn(OrdenController.class).listDetalles(o.getId(), PageRequest.of(0,50))).withRel("detalles"),
        linkTo(methodOn(OrdenController.class).confirmar(o.getId())).withRel("confirmar"),
        linkTo(methodOn(OrdenController.class).resumen(o.getId())).withRel("resumen")
      );
    }
  }

  static class DetalleAssembler implements RepresentationModelAssembler<Detalle, EntityModel<Detalle>> {
    @Override public EntityModel<Detalle> toModel(Detalle d) {
      return EntityModel.of(d,
        linkTo(methodOn(OrdenController.class).getDetalle(d.getOrdenId(), d.getId())).withSelfRel(),
        linkTo(methodOn(OrdenController.class).listDetalles(d.getOrdenId(), PageRequest.of(0,50))).withRel("collection")
      );
    }
  }
}

