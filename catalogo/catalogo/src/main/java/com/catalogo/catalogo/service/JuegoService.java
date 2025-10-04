package com.catalogo.catalogo.service;

import com.catalogo.catalogo.model.Juego;
import com.catalogo.catalogo.repository.JuegoRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class JuegoService {

  private final JuegoRepository repo;

  public JuegoService(JuegoRepository repo) { this.repo = repo; }

  public Page<Juego> listar(String q, String categoriaId, String generoId, String estadoId, Pageable p) {
    if (q != null && !q.isBlank())        return repo.findByNombreJuegoContainingIgnoreCase(q, p);
    if (categoriaId != null && !categoriaId.isBlank()) return repo.findByCategoriaId(categoriaId, p);
    if (generoId != null && !generoId.isBlank())       return repo.findByGeneroId(generoId, p);
    if (estadoId != null && !estadoId.isBlank())       return repo.findByEstadoId(estadoId, p);
    return repo.findAll(p);
  }

  public Juego obtener(String id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Juego no encontrado: " + id));
  }

  public Juego crear(Juego j) {
    if (repo.existsById(j.getId())) throw new IllegalArgumentException("ID ya existe: " + j.getId());
    return repo.save(j);
  }

  public Juego actualizar(String id, Juego data) {
    Juego j = obtener(id);
    j.setNombreJuego(data.getNombreJuego());
    j.setPrecio(data.getPrecio());
    j.setFotoJuego(data.getFotoJuego());
    j.setFechaLanzamiento(data.getFechaLanzamiento());
    j.setCategoriaId(data.getCategoriaId());
    j.setGeneroId(data.getGeneroId());
    j.setEstadoId(data.getEstadoId());
    return repo.save(j);
  }

  public void eliminar(String id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("Juego no encontrado: " + id);
    repo.deleteById(id);
  }
}