package com.orden.orden.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;

@FeignClient(name = "usuarioClient", url = "${clients.usuario.base-url}")
public interface UsuarioClient {

  @GetMapping("/internal/usuarios/{id}")
  UsuarioResponse getUsuario(@PathVariable("id") String id);

  record UsuarioResponse(
      String id,
      String nombreUsuario,
      String email,
      String telefono,
      String rolId,
      String estadoId,
      Instant creadoEn) {}
}




