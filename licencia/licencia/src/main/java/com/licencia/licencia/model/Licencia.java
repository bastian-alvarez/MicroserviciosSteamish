package com.licencia.licencia.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "licencia")
public class Licencia {

  @Id
  @Column(name = "id_licencia", length = 20, nullable = false)
  private String id;

  @NotBlank
  @Column(name = "clave", nullable = false, unique = true)
  private String clave;

  @NotNull
  @Column(name = "fecha_vencimiento", nullable = false)
  private LocalDate fechaVencimiento;

  // Referencias a otros microservicios (como IDs simples)
  @Column(name = "id_estado", length = 20)
  private String estadoId;

  @Column(name = "id_juego", length = 20)
  private String juegoId;

  // getters y setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getClave() { return clave; }
  public void setClave(String clave) { this.clave = clave; }

  public LocalDate getFechaVencimiento() { return fechaVencimiento; }
  public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

  public String getEstadoId() { return estadoId; }
  public void setEstadoId(String estadoId) { this.estadoId = estadoId; }

  public String getJuegoId() { return juegoId; }
  public void setJuegoId(String juegoId) { this.juegoId = juegoId; }
}
