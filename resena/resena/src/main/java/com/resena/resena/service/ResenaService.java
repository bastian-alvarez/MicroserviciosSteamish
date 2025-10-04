package com.resena.resena.service;


import com.resena.resena.model.Resena;
import com.resena.resena.repository.ResenaRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ResenaService {

  private final ResenaRepository repo;

  public ResenaService(ResenaRepository repo) { this.repo = repo; }

  public Page<Resena> listar(String q, String juegoId, String usuarioId, String estadoId, Pageable p) {
    if (q != null && !q.isBlank())                 return repo.findByComentarioContainingIgnoreCase(q, p);
    if (juegoId != null && estadoId != null
        && !juegoId.isBlank() && !estadoId.isBlank()) return repo.findByJuegoIdAndEstadoId(juegoId, estadoId, p);
    if (juegoId != null && !juegoId.isBlank())     return repo.findByJuegoId(juegoId, p);
    if (usuarioId != null && !usuarioId.isBlank()) return repo.findByUsuarioId(usuarioId, p);
    if (estadoId != null && !estadoId.isBlank())   return repo.findByEstadoId(estadoId, p);
    return repo.findAll(p);
  }

  public Resena obtener(String id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada: " + id));
  }

  public Resena crear(Resena r) {
    if (repo.existsById(r.getId())) throw new IllegalArgumentException("Ya existe reseña con id: " + r.getId());
    return repo.save(r);
  }

  public Resena actualizar(String id, Resena data) {
    var r = obtener(id);
    r.setCalificacion(data.getCalificacion());
    r.setComentario(data.getComentario());
    r.setEstadoId(data.getEstadoId());
    // usuarioId/juegoId normalmente NO cambian; si necesitas mover, habilítalo:
    // r.setUsuarioId(data.getUsuarioId()); r.setJuegoId(data.getJuegoId());
    return repo.save(r);
  }

  public void eliminar(String id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("Reseña no encontrada: " + id);
    repo.deleteById(id);
  }

  /** Moderación / cambio de estado (APROBAR, RECHAZAR, BANEAR, etc.) */
  public Resena cambiarEstado(String id, String nuevoEstadoId) {
    var r = obtener(id);
    r.setEstadoId(nuevoEstadoId);
    return repo.save(r);
  }

  /** Promedio de calificaciones para un juego (considera todas o sólo APROBADAS según tu uso) */
  public BigDecimal promedioPorJuego(String juegoId, boolean soloAprobadas) {
    var page = (soloAprobadas)
        ? repo.findByJuegoIdAndEstadoId(juegoId, "APROBADA", PageRequest.of(0, Integer.MAX_VALUE))
        : repo.findByJuegoId(juegoId, PageRequest.of(0, Integer.MAX_VALUE));
    var list = page.getContent();
    if (list.isEmpty()) return BigDecimal.ZERO;
    var sum = list.stream().map(Resena::getCalificacion).map(BigDecimal::valueOf)
                  .reduce(BigDecimal.ZERO, BigDecimal::add);
    return sum.divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
  }
}
