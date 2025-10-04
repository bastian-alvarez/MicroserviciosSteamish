package com.usuario.usuario.controller;

import com.usuario.usuario.model.Usuario;
import com.usuario.usuario.service.UsuarioService;
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

@Tag(name = "Usuarios", description = "CRUD y acciones sobre usuarios")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  private final UsuarioService service;
  private final UsuarioAssembler assembler = new UsuarioAssembler();

  public UsuarioController(UsuarioService service) { this.service = service; }

  // LISTAR
  @Operation(summary = "Listar usuarios (paginado y filtros por q/rolId/estadoId)")
  @GetMapping
  public CollectionModel<EntityModel<Usuario>> list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String rolId,
      @RequestParam(required = false) String estadoId,
      @PageableDefault(size = 20, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<Usuario> page = service.listar(q, rolId, estadoId, pageable);
    var models = page.getContent().stream().map(assembler::toModel).toList();

    return CollectionModel.of(models,
      linkTo(methodOn(UsuarioController.class).list(q, rolId, estadoId, pageable)).withSelfRel(),
      linkTo(methodOn(UsuarioController.class).create(null)).withRel("create"));
  }

  // OBTENER
  @Operation(summary = "Obtener usuario por id")
  @GetMapping("/{id}")
  public EntityModel<Usuario> get(@PathVariable String id) {
    return assembler.toModel(service.obtener(id));
  }

  // BUSCAR por email
  @Operation(summary = "Buscar usuario por email")
  @GetMapping("/buscar")
  public EntityModel<Usuario> buscarPorEmail(@RequestParam String email) {
    return assembler.toModel(service.buscarPorEmailObligatorio(email));
  }

  // CREAR
  @Operation(summary = "Crear usuario")
  @PostMapping
  public ResponseEntity<EntityModel<Usuario>> create(@Valid @RequestBody Usuario body) {
    var saved = service.crear(body);
    return ResponseEntity
      .created(linkTo(methodOn(UsuarioController.class).get(saved.getId())).toUri())
      .body(assembler.toModel(saved));
  }

  // ACTUALIZAR (datos generales; no password)
  @Operation(summary = "Actualizar datos de usuario (no incluye password)")
  @PutMapping("/{id}")
  public EntityModel<Usuario> update(@PathVariable String id, @Valid @RequestBody Usuario body) {
    return assembler.toModel(service.actualizar(id, body));
  }

  // ELIMINAR
  @Operation(summary = "Eliminar usuario")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  // ACCIONES: cambiar password / rol / estado
  @Operation(summary = "Cambiar password (requiere enviar hash)")
  @PostMapping("/{id}/password")
  public EntityModel<Usuario> cambiarPassword(@PathVariable String id, @RequestParam String passwordHash) {
    return assembler.toModel(service.cambiarPassword(id, passwordHash));
  }

  @Operation(summary = "Cambiar rol")
  @PostMapping("/{id}/rol")
  public EntityModel<Usuario> cambiarRol(@PathVariable String id, @RequestParam String rolId) {
    return assembler.toModel(service.cambiarRol(id, rolId));
  }

  @Operation(summary = "Cambiar estado")
  @PostMapping("/{id}/estado")
  public EntityModel<Usuario> cambiarEstado(@PathVariable String id, @RequestParam String estadoId) {
    return assembler.toModel(service.cambiarEstado(id, estadoId));
  }

  // Manejo simple de errores
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(java.util.Map.of("error","BAD_REQUEST","message",ex.getMessage()));
  }

  // HATEOAS Assembler
  static class UsuarioAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {
    @Override public EntityModel<Usuario> toModel(Usuario u) {
      return EntityModel.of(u,
        linkTo(methodOn(UsuarioController.class).get(u.getId())).withSelfRel(),
        linkTo(methodOn(UsuarioController.class).list(null, u.getRolId(), u.getEstadoId(), PageRequest.of(0,20))).withRel("collection"),
        linkTo(methodOn(UsuarioController.class).cambiarPassword(u.getId(), null)).withRel("cambiar_password"),
        linkTo(methodOn(UsuarioController.class).cambiarRol(u.getId(), null)).withRel("cambiar_rol"),
        linkTo(methodOn(UsuarioController.class).cambiarEstado(u.getId(), null)).withRel("cambiar_estado")
      );
    }
  }
}
