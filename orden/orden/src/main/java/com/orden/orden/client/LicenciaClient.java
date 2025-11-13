package com.orden.orden.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "licenciaClient", url = "${clients.licencia.base-url}")
public interface LicenciaClient {

  @GetMapping("/internal/licencias/{id}")
  LicenciaResponse obtenerLicencia(@PathVariable("id") String id);

  @GetMapping("/internal/licencias/juego/{juegoId}")
  List<LicenciaResponse> licenciasPorJuego(
      @PathVariable("juegoId") String juegoId,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size);

  record LicenciaResponse(
      String id,
      String clave,
      LocalDate fechaVencimiento,
      String estadoId,
      String juegoId) {}
}


