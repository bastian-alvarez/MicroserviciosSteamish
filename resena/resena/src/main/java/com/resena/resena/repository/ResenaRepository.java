package com.resena.resena.repository;


import com.resena.resena.model.Resena;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaRepository extends JpaRepository<Resena, String> {

  Page<Resena> findByJuegoId(String juegoId, Pageable pageable);

  Page<Resena> findByUsuarioId(String usuarioId, Pageable pageable);

  Page<Resena> findByEstadoId(String estadoId, Pageable pageable);

  Page<Resena> findByJuegoIdAndEstadoId(String juegoId, String estadoId, Pageable pageable);

  Page<Resena> findByComentarioContainingIgnoreCase(String q, Pageable pageable);

  /** Para promedio por juego: podrías usar consulta nativa o calcular en service (stream). */
}
