package com.orden.orden.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle")
public class Detalle {

  @Id
  @Column(name = "id_detalle", length = 20, nullable = false)
  private String id;

  @NotNull @DecimalMin("0.0")
  @Column(name = "subtotal", precision = 14, scale = 2, nullable = false)
  private BigDecimal subtotal;

  @NotNull @Min(1)
  @Column(name = "cantidad", nullable = false)
  private Integer cantidad;

  @NotNull @DecimalMin("0.0")
  @Column(name = "iva", precision = 14, scale = 2, nullable = false)
  private BigDecimal iva;

  // Referencias por ID (otros MS/BD)
  @Column(name = "id_orden", length = 20, nullable = false)
  private String ordenId;

  @Column(name = "id_licencia", length = 20)
  private String licenciaId;

  // Getters/Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public BigDecimal getSubtotal() { return subtotal; }
  public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
  public Integer getCantidad() { return cantidad; }
  public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
  public BigDecimal getIva() { return iva; }
  public void setIva(BigDecimal iva) { this.iva = iva; }
  public String getOrdenId() { return ordenId; }
  public void setOrdenId(String ordenId) { this.ordenId = ordenId; }
  public String getLicenciaId() { return licenciaId; }
  public void setLicenciaId(String licenciaId) { this.licenciaId = licenciaId; }

  /** Total de esta línea = subtotal + iva (puedes ajustar si tu regla es otra) */
  @Transient
  public BigDecimal getTotalLinea() {
    return (subtotal == null ? BigDecimal.ZERO : subtotal)
         .add(iva == null ? BigDecimal.ZERO : iva);
  }
}
