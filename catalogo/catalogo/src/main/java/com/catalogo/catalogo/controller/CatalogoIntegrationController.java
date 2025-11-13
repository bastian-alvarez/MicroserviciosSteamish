package com.catalogo.catalogo.controller;

import com.catalogo.catalogo.model.Juego;
import com.catalogo.catalogo.service.JuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints internos pensados para comunicación entre microservicios.
 * Devuelven directamente los datos del dominio (sin HATEOAS) para
 * facilitar el consumo vía Feign/RestTemplate.
 */
@RestController
@Tag(name = "Integración Catálogo", description = "Endpoints internos para otros microservicios")
@RequestMapping("/internal/catalogo")
public class CatalogoIntegrationController {

  private final JuegoService service;

  public CatalogoIntegrationController(JuegoService service) {
    this.service = service;
  }

  @Operation(summary = "Obtener un juego sin HATEOAS por ID")
  @GetMapping("/juegos/{id}")
  public Juego getJuegoRaw(@PathVariable String id) {
    return service.obtener(id);
  }

  @Operation(summary = "Listar juegos para consumo interno")
  @GetMapping("/juegos")
  public List<Juego> listJuegosRaw(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String categoriaId,
      @RequestParam(required = false) String generoId,
      @RequestParam(required = false) String estadoId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {

    Pageable pageable = PageRequest.of(page, Math.min(size, 100));
    return service.listar(q, categoriaId, generoId, estadoId, pageable).getContent();
  }
}


