package com.catalogo.catalogo.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "juego")
public class Juego {

  @Id
  @Column(name = "id_juego", length = 20, nullable = false)
  private String id;

  @NotBlank
  @Column(name = "nombre_juego", nullable = false)
  private String nombreJuego;

  @NotNull @DecimalMin("0.0")
  @Column(name = "precio", precision = 12, scale = 2, nullable = false)
  private BigDecimal precio;

  @Column(name = "foto_juego")
  private String fotoJuego;

  @NotNull
  @Column(name = "fecha_lanzamiento", nullable = false)
  private LocalDate fechaLanzamiento;

  // Referencias por ID (otros MS/DBs)
  @Column(name = "id_categoria", length = 20) private String categoriaId;
  @Column(name = "id_genero",    length = 20) private String generoId;
  @Column(name = "id_estado",    length = 20) private String estadoId;

  // getters/setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getNombreJuego() { return nombreJuego; }
  public void setNombreJuego(String nombreJuego) { this.nombreJuego = nombreJuego; }
  public BigDecimal getPrecio() { return precio; }
  public void setPrecio(BigDecimal precio) { this.precio = precio; }
  public String getFotoJuego() { return fotoJuego; }
  public void setFotoJuego(String fotoJuego) { this.fotoJuego = fotoJuego; }
  public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
  public void setFechaLanzamiento(LocalDate fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }
  public String getCategoriaId() { return categoriaId; }
  public void setCategoriaId(String categoriaId) { this.categoriaId = categoriaId; }
  public String getGeneroId() { return generoId; }
  public void setGeneroId(String generoId) { this.generoId = generoId; }
  public String getEstadoId() { return estadoId; }
  public void setEstadoId(String estadoId) { this.estadoId = estadoId; }
}
