package com.juegos.juegos.repository;

import com.juegos.juegos.model.Juego;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JuegoRepository extends JpaRepository<Juego, String> {
  Page<Juego> findByNombreJuegoContainingIgnoreCase(String q, Pageable pageable);
  Page<Juego> findByCategoriaId(String categoriaId, Pageable pageable);
  Page<Juego> findByGeneroId(String generoId, Pageable pageable);
  Page<Juego> findByEstadoId(String estadoId, Pageable pageable);
}
