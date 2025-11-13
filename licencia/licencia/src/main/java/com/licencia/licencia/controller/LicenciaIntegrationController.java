package com.licencia.licencia.controller;

import com.licencia.licencia.model.Licencia;
import com.licencia.licencia.service.LicenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoints internos pensados para consumo por otros microservicios.
 * Se devuelven datos sin HATEOAS para simplificar la integración.
 */
@RestController
@RequestMapping("/internal/licencias")
@Tag(name = "Integración Licencias", description = "Operaciones internas para compartir licencias")
public class LicenciaIntegrationController {

  private final LicenciaService service;

  public LicenciaIntegrationController(LicenciaService service) {
    this.service = service;
  }

  @Operation(summary = "Obtener licencia por ID sin HATEOAS")
  @GetMapping("/{id}")
  public Licencia getLicenciaRaw(@PathVariable String id) {
    return service.obtener(id);
  }

  @Operation(summary = "Listar licencias por juego para otros microservicios")
  @GetMapping("/juego/{juegoId}")
  public List<Licencia> listPorJuego(
      @PathVariable String juegoId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return service
        .listar(juegoId, null, PageRequest.of(page, Math.min(size, 100)))
        .getContent();
  }

  @Operation(summary = "Licencias disponibles para un juego")
  @GetMapping("/juego/{juegoId}/disponibles")
  public List<Licencia> listDisponibles(
      @PathVariable String juegoId,
      @RequestParam(defaultValue = "20") int size) {
    return service.listarDisponibles(juegoId, PageRequest.of(0, Math.min(size, 100)));
  }

  @Operation(summary = "Asignar licencia y marcarla como usada")
  @PostMapping("/{id}/assign")
  public Licencia assign(@PathVariable String id, @RequestBody Map<String, String> payload) {
    String usuarioId = payload.get("usuarioId");
    if (usuarioId == null || usuarioId.isBlank()) {
      throw new IllegalArgumentException("usuarioId es obligatorio");
    }
    return service.asignar(id, usuarioId.trim());
  }

  @Operation(summary = "Liberar licencia")
  @PostMapping("/{id}/release")
  public Licencia release(@PathVariable String id) {
    return service.liberar(id);
  }
}


