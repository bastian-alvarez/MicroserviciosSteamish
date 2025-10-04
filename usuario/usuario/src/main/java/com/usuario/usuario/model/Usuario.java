package com.usuario.usuario.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;

@Entity
@Table(name = "usuario",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
       })
public class Usuario {

  @Id
  @Column(name = "id_usuario", length = 20, nullable = false)
  private String id;

  @NotBlank
  @Column(name = "nombre_usuario", nullable = false)
  private String nombreUsuario;

  @NotBlank @Email
  @Column(name = "email", nullable = false)
  private String email;

  /** Guardamos hash; WRITE_ONLY para no exponerlo en respuestas JSON */
  @NotBlank
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "password_hash", nullable = false, length = 200)
  private String passwordHash;

  @Column(name = "telefono", length = 30)
  private String telefono;

  /** Referencias a otros MS (o catálogos) por ID */
  @Column(name = "id_rol", length = 20)
  private String rolId;

  @Column(name = "id_estado", length = 20)
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
  public String getNombreUsuario() { return nombreUsuario; }
  public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public String getTelefono() { return telefono; }
  public void setTelefono(String telefono) { this.telefono = telefono; }
  public String getRolId() { return rolId; }
  public void setRolId(String rolId) { this.rolId = rolId; }
  public String getEstadoId() { return estadoId; }
  public void setEstadoId(String estadoId) { this.estadoId = estadoId; }
  public Instant getCreadoEn() { return creadoEn; }
  public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}

