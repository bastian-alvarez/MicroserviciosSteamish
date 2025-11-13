package com.resena.resena.controller;

import com.resena.resena.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Endpoints simplificados para uso interno entre microservicios.
 */
@RestController
@Tag(name = "Integración Reseñas", description = "Operaciones internas para otros microservicios")
@RequestMapping("/internal/resenas")
public class ResenaIntegrationController {

  private final ResenaService service;

  public ResenaIntegrationController(ResenaService service) {
    this.service = service;
  }

  @Operation(summary = "Promedio de calificaciones para un juego")
  @GetMapping("/promedio/{juegoId}")
  public BigDecimal promedio(
      @PathVariable String juegoId,
      @RequestParam(defaultValue = "true") boolean soloAprobadas) {
    return service.promedioPorJuego(juegoId, soloAprobadas);
  }
}


