package com.orden.orden.repository;

import com.orden.orden.model.OrdenCompra;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<OrdenCompra, String> {
  Page<OrdenCompra> findByUsuarioId(String usuarioId, Pageable pageable);
  Page<OrdenCompra> findByEstadoId(String estadoId, Pageable pageable);
}

