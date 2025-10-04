package com.usuario.usuario.repository;


import com.usuario.usuario.model.Usuario;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

  Optional<Usuario> findByEmail(String email);

  Page<Usuario> findByNombreUsuarioContainingIgnoreCase(String q, Pageable pageable);

  Page<Usuario> findByRolId(String rolId, Pageable pageable);

  Page<Usuario> findByEstadoId(String estadoId, Pageable pageable);
}

