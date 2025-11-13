package com.licencia.licencia.repository;


import com.licencia.licencia.model.Licencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenciaRepository extends JpaRepository<Licencia, String> {
  Optional<Licencia> findByClave(String clave);
  Page<Licencia> findByJuegoId(String juegoId, Pageable pageable);
  Page<Licencia> findByEstadoId(String estadoId, Pageable pageable);
  Page<Licencia> findByJuegoIdAndEstadoId(String juegoId, String estadoId, Pageable pageable);
  Optional<Licencia> findFirstByJuegoIdAndEstadoIdOrderByFechaVencimientoAsc(String juegoId, String estadoId);
}
