package com.catalogo.catalogo.repository;

import com.catalogo.catalogo.model.Juego;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JuegoRepository extends JpaRepository<Juego, String> {
  Page<Juego> findByNombreJuegoContainingIgnoreCase(String q, Pageable p);
  Page<Juego> findByCategoriaId(String categoriaId, Pageable p);
  Page<Juego> findByGeneroId(String generoId, Pageable p);
  Page<Juego> findByEstadoId(String estadoId, Pageable p);
}