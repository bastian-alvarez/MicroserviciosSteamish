package com.usuario.usuario.controller;

import com.usuario.usuario.model.Usuario;
import com.usuario.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints internos expuestos sin HATEOAS para facilitar consumo desde otros microservicios.
 */
@RestController
@Tag(name = "Integración Usuarios", description = "Operaciones internas sin HATEOAS")
@RequestMapping("/internal/usuarios")
public class UsuarioIntegrationController {

  private final UsuarioService service;

  public UsuarioIntegrationController(UsuarioService service) {
    this.service = service;
  }

  @Operation(summary = "Obtener usuario por ID (sin HATEOAS)")
  @GetMapping("/{id}")
  public Usuario getUsuarioRaw(@PathVariable String id) {
    return service.obtener(id);
  }
}


