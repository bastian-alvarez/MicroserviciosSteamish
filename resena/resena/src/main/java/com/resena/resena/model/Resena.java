package com.resena.resena.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;

@Entity
@Table(name = "resena")
public class Resena {

  @Id
  @Column(name = "id_resena", length = 20, nullable = false)
  private String id;

  /** 1..5 */
  @NotNull
  @Min(1) @Max(5)
  @Column(name = "calificacion", nullable = false)
  private Integer calificacion;

  @NotBlank
  @Column(name = "comentario", nullable = false, length = 2000)
  private String comentario;

  /** referencias a otros MS/BD por ID */
  @NotBlank
  @Column(name = "id_usuario", length = 20, nullable = false)
  private String usuarioId;

  @NotBlank
  @Column(name = "id_juego", length = 20, nullable = false)
  private String juegoId;

  /** estadoId: ej. PENDIENTE | APROBADA | RECHAZADA | BANEADA */
  @NotBlank
  @Column(name = "id_estado", length = 20, nullable = false)
  private String estadoId;

  @NotNull
  @Column(name = "creado_en", nullable = false)
  private Instant creadoEn = Instant.now();

  @PrePersist
  public void prePersist() {
    if (creadoEn == null) creadoEn = Instant.now();
  }

  // Getters/Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public Integer getCalificacion() { return calificacion; }
  public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }

  public String getComentario() { return comentario; }
  public void setComentario(String comentario) { this.comentario = comentario; }

  public String getUsuarioId() { return usuarioId; }
  public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

  public String getJuegoId() { return juegoId; }
  public void setJuegoId(String juegoId) { this.juegoId = juegoId; }

  public String getEstadoId() { return estadoId; }
  public void setEstadoId(String estadoId) { this.estadoId = estadoId; }

  public Instant getCreadoEn() { return creadoEn; }
  public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
