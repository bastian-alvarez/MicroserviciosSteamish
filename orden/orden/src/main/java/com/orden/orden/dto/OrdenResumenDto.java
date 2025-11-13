package com.orden.orden.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrdenResumenDto(
    String id,
    String estadoId,
    BigDecimal total,
    Instant creadoEn,
    Usuario usuario,
    List<DetalleResumenDto> detalles) {

  public record Usuario(
      String id,
      String nombre,
      String email,
      String telefono,
      String rolId,
      String estadoId,
      Instant creadoEn) {}

  public record DetalleResumenDto(
      String id,
      String juegoId,
      String juegoNombre,
      String juegoEstado,
      BigDecimal precioUnitario,
      Integer cantidad,
      BigDecimal subtotal,
      BigDecimal iva,
      BigDecimal totalLinea,
      BigDecimal promedioCalificacion,
      String licenciaId,
      String licenciaClave,
      String licenciaEstado) {}
}




