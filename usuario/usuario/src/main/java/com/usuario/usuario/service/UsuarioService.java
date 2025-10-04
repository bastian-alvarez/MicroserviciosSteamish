package com.usuario.usuario.service;

import com.usuario.usuario.model.Usuario;
import com.usuario.usuario.repository.UsuarioRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UsuarioService {

  private final UsuarioRepository repo;

  public UsuarioService(UsuarioRepository repo) { this.repo = repo; }

  public Page<Usuario> listar(String q, String rolId, String estadoId, Pageable p) {
    if (StringUtils.hasText(q))        return repo.findByNombreUsuarioContainingIgnoreCase(q, p);
    if (StringUtils.hasText(rolId))    return repo.findByRolId(rolId, p);
    if (StringUtils.hasText(estadoId)) return repo.findByEstadoId(estadoId, p);
    return repo.findAll(p);
  }

  public Usuario obtener(String id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
  }

  public Usuario crear(Usuario u) {
    if (repo.existsById(u.getId()))
      throw new IllegalArgumentException("Ya existe usuario con id: " + u.getId());
    repo.findByEmail(u.getEmail()).ifPresent(x -> {
      throw new IllegalArgumentException("Email ya registrado: " + u.getEmail());
    });
    return repo.save(u);
  }

  public Usuario actualizar(String id, Usuario data) {
    var u = obtener(id);
    if (!u.getEmail().equalsIgnoreCase(data.getEmail())) {
      repo.findByEmail(data.getEmail()).ifPresent(x -> {
        throw new IllegalArgumentException("Email ya registrado: " + data.getEmail());
      });
      u.setEmail(data.getEmail());
    }
    u.setNombreUsuario(data.getNombreUsuario());
    u.setTelefono(data.getTelefono());
    u.setRolId(data.getRolId());
    u.setEstadoId(data.getEstadoId());
    // passwordHash no se actualiza aquí (usa cambiarPassword)
    return repo.save(u);
  }

  public void eliminar(String id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("Usuario no encontrado: " + id);
    repo.deleteById(id);
  }

  /** Acciones específicas **/

  public Usuario cambiarPassword(String id, String nuevoHash) {
    var u = obtener(id);
    u.setPasswordHash(nuevoHash);
    return repo.save(u);
  }

  public Usuario cambiarRol(String id, String nuevoRolId) {
    var u = obtener(id);
    u.setRolId(nuevoRolId);
    return repo.save(u);
  }

  public Usuario cambiarEstado(String id, String nuevoEstadoId) {
    var u = obtener(id);
    u.setEstadoId(nuevoEstadoId);
    return repo.save(u);
  }

  public Usuario buscarPorEmailObligatorio(String email) {
    return repo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("No existe usuario con email: " + email));
  }
}
