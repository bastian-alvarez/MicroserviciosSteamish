package com.orden.orden.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orden_compra")
public class OrdenCompra {

  @Id
  @Column(name = "id_orden", length = 20, nullable = false)
  private String id;

  @NotNull
  @Column(name = "total", precision = 14, scale = 2, nullable = false)
  private BigDecimal total = BigDecimal.ZERO;

  // Referencias a otros MS/BD por ID
  @Column(name = "id_usuario", length = 20)
  private String usuarioId;

  @Column(name = "id_estado", length = 20)
  private String estadoId;

  @NotNull
  @Column(name = "creado_en", nullable = false)
  private Instant creadoEn = Instant.now();

  // Getters/Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public BigDecimal getTotal() { return total; }
  public void setTotal(BigDecimal total) { this.total = total; }
  public String getUsuarioId() { return usuarioId; }
  public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
  public String getEstadoId() { return estadoId; }
  public void setEstadoId(String estadoId) { this.estadoId = estadoId; }
  public Instant getCreadoEn() { return creadoEn; }
  public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
