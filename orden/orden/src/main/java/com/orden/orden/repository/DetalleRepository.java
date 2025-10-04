package com.orden.orden.repository;


import com.orden.orden.model.Detalle;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleRepository extends JpaRepository<Detalle, String> {
  Page<Detalle> findByOrdenId(String ordenId, Pageable pageable);
  long deleteByOrdenId(String ordenId);
}
