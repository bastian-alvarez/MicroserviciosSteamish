package com.licencia.licencia.controller;

import com.licencia.licencia.model.Licencia;
import com.licencia.licencia.service.LicenciaService;
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

@Tag(name = "Licencias", description = "CRUD del microservicio de Licencias")
@RestController
@RequestMapping("/api/licencias")
public class LicenciaController {

  private final LicenciaService service;
  private final LicenciaAssembler assembler = new LicenciaAssembler();

  public LicenciaController(LicenciaService service) { this.service = service; }

  @Operation(summary = "Listar licencias (paginado y filtros)")
  @GetMapping
  public CollectionModel<EntityModel<Licencia>> list(
      @RequestParam(required = false) String juegoId,
      @RequestParam(required = false) String estadoId,
      @PageableDefault(size = 20, sort = "fechaVencimiento") Pageable pageable) {

    Page<Licencia> page = service.listar(juegoId, estadoId, pageable);
    var models = page.getContent().stream().map(assembler::toModel).toList();

    return CollectionModel.of(models,
      linkTo(methodOn(LicenciaController.class).list(juegoId, estadoId, pageable)).withSelfRel(),
      linkTo(methodOn(LicenciaController.class).create(null)).withRel("create"));
  }

  @Operation(summary = "Obtener licencia por id")
  @GetMapping("/{id}")
  public EntityModel<Licencia> get(@PathVariable String id) {
    return assembler.toModel(service.obtener(id));
  }

  @Operation(summary = "Buscar licencia por clave")
  @GetMapping("/buscar")
  public ResponseEntity<EntityModel<Licencia>> buscarPorClave(@RequestParam String clave) {
    return service.buscarPorClave(clave)
      .map(l -> ResponseEntity.ok(assembler.toModel(l)))
      .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Crear licencia")
  @PostMapping
  public ResponseEntity<EntityModel<Licencia>> create(@Valid @RequestBody Licencia body) {
    var saved = service.crear(body);
    return ResponseEntity
      .created(linkTo(methodOn(LicenciaController.class).get(saved.getId())).toUri())
      .body(assembler.toModel(saved));
  }

  @Operation(summary = "Actualizar licencia")
  @PutMapping("/{id}")
  public EntityModel<Licencia> update(@PathVariable String id, @Valid @RequestBody Licencia body) {
    return assembler.toModel(service.actualizar(id, body));
  }

  @Operation(summary = "Eliminar licencia")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(java.util.Map.of("error","BAD_REQUEST","message",ex.getMessage()));
  }

  // ---- HATEOAS assembler ----
  static class LicenciaAssembler implements RepresentationModelAssembler<Licencia, EntityModel<Licencia>> {
    @Override public EntityModel<Licencia> toModel(Licencia l) {
      return EntityModel.of(l,
        linkTo(methodOn(LicenciaController.class).get(l.getId())).withSelfRel(),
        linkTo(methodOn(LicenciaController.class).list(null,null,PageRequest.of(0,20))).withRel("collection"));
    }
  }
}

