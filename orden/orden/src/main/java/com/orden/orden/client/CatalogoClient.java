package com.orden.orden.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDate;

@FeignClient(name = "catalogoClient", url = "${clients.catalogo.base-url}")
public interface CatalogoClient {

  @GetMapping("/internal/catalogo/juegos/{id}")
  JuegoResponse getJuego(@PathVariable("id") String id);

  record JuegoResponse(
      String id,
      String nombreJuego,
      BigDecimal precio,
      String fotoJuego,
      LocalDate fechaLanzamiento,
      String categoriaId,
      String generoId,
      String estadoId) {}
}




