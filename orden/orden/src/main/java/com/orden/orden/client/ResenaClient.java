package com.orden.orden.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "resenaClient", url = "${clients.resena.base-url}")
public interface ResenaClient {

  @GetMapping("/internal/resenas/promedio/{juegoId}")
  BigDecimal obtenerPromedio(
      @PathVariable("juegoId") String juegoId,
      @RequestParam(name = "soloAprobadas", defaultValue = "true") boolean soloAprobadas);
}




