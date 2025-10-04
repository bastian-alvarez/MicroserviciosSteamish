package com.licencia.licencia.service;


import com.licencia.licencia.model.Licencia;
import com.licencia.licencia.repository.LicenciaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LicenciaService {

  private final LicenciaRepository repo;

  public LicenciaService(LicenciaRepository repo) { this.repo = repo; }

  public Page<Licencia> listar(String juegoId, String estadoId, Pageable p) {
    if (juegoId != null && !juegoId.isBlank()) return repo.findByJuegoId(juegoId, p);
    if (estadoId != null && !estadoId.isBlank()) return repo.findByEstadoId(estadoId, p);
    return repo.findAll(p);
  }

  public Licencia obtener(String id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Licencia no encontrada: " + id));
  }

  public Licencia crear(Licencia l) {
    if (repo.existsById(l.getId())) throw new IllegalArgumentException("Ya existe licencia con id: " + l.getId());
    return repo.save(l);
  }

  public Licencia actualizar(String id, Licencia data) {
    var l = obtener(id);
    l.setClave(data.getClave());
    l.setFechaVencimiento(data.getFechaVencimiento());
    l.setEstadoId(data.getEstadoId());
    l.setJuegoId(data.getJuegoId());
    return repo.save(l);
  }

  public void eliminar(String id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("Licencia no encontrada: " + id);
    repo.deleteById(id);
  }

  public Optional<Licencia> buscarPorClave(String clave) {
    return repo.findByClave(clave);
  }
}

