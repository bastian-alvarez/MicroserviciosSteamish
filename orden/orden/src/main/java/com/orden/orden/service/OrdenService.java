package com.orden.orden.service;

import com.orden.orden.client.CatalogoClient;
import com.orden.orden.client.LicenciaClient;
import com.orden.orden.client.ResenaClient;
import com.orden.orden.client.UsuarioClient;
import com.orden.orden.dto.OrdenResumenDto;
import com.orden.orden.model.Detalle;
import com.orden.orden.model.OrdenCompra;
import com.orden.orden.repository.DetalleRepository;
import com.orden.orden.repository.OrdenRepository;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenService {

  private static final BigDecimal IVA_RATE = new BigDecimal("0.12");

  private final OrdenRepository ordenRepo;
  private final DetalleRepository detalleRepo;
  private final CatalogoClient catalogoClient;
  private final UsuarioClient usuarioClient;
  private final ResenaClient resenaClient;
  private final LicenciaClient licenciaClient;

  public OrdenService(OrdenRepository ordenRepo,
                      DetalleRepository detalleRepo,
                      CatalogoClient catalogoClient,
                      UsuarioClient usuarioClient,
                      ResenaClient resenaClient,
                      LicenciaClient licenciaClient) {
    this.ordenRepo = ordenRepo;
    this.detalleRepo = detalleRepo;
    this.catalogoClient = catalogoClient;
    this.usuarioClient = usuarioClient;
    this.resenaClient = resenaClient;
    this.licenciaClient = licenciaClient;
  }

  // ---- Órdenes ----
  public Page<OrdenCompra> listar(String usuarioId, String estadoId, Pageable p) {
    if (usuarioId != null && !usuarioId.isBlank()) return ordenRepo.findByUsuarioId(usuarioId, p);
    if (estadoId != null && !estadoId.isBlank())   return ordenRepo.findByEstadoId(estadoId, p);
    return ordenRepo.findAll(p);
  }

  public OrdenCompra obtener(String id) {
    return ordenRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + id));
  }

  public OrdenCompra crear(OrdenCompra oc) {
    if (ordenRepo.existsById(oc.getId())) throw new IllegalArgumentException("Ya existe orden con id: " + oc.getId());
    validarUsuario(oc.getUsuarioId());
    oc.setTotal(BigDecimal.ZERO);
    return ordenRepo.save(oc);
  }

  public OrdenCompra actualizar(String id, OrdenCompra data) {
    var oc = obtener(id);
    if (data.getUsuarioId() != null && !data.getUsuarioId().equals(oc.getUsuarioId())) {
      validarUsuario(data.getUsuarioId());
      oc.setUsuarioId(data.getUsuarioId());
    }
    oc.setEstadoId(data.getEstadoId());
    return ordenRepo.save(oc);
  }

  public void eliminar(String id) {
    if (!ordenRepo.existsById(id)) throw new IllegalArgumentException("Orden no encontrada: " + id);
    detalleRepo.deleteByOrdenId(id);   // limpia detalles
    ordenRepo.deleteById(id);
  }

  // ---- Detalles ----
  public Page<Detalle> listarDetalles(String ordenId, Pageable p) {
    return detalleRepo.findByOrdenId(ordenId, p);
  }

  public Detalle obtenerDetalle(String id) {
    return detalleRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado: " + id));
  }

  public Detalle agregarDetalle(String ordenId, Detalle d) {
    obtener(ordenId); // valida que exista la orden
    d.setOrdenId(ordenId);
    aplicarPreciosDesdeCatalogo(d);
    asignarLicenciaDisponible(d);
    var saved = detalleRepo.save(d);
    recalcularTotal(ordenId);
    return saved;
  }

  public Detalle actualizarDetalle(String ordenId, String detalleId, Detalle data) {
    var det = obtenerDetalle(detalleId);
    if (!ordenId.equals(det.getOrdenId())) throw new IllegalArgumentException("Detalle no pertenece a la orden");

    if (data.getJuegoId() != null && !data.getJuegoId().isBlank() && !data.getJuegoId().equals(det.getJuegoId())) {
      det.setJuegoId(data.getJuegoId());
    }
    det.setCantidad(data.getCantidad());
    aplicarPreciosDesdeCatalogo(det);
    if (data.getLicenciaId() != null && !data.getLicenciaId().isBlank()) {
      det.setLicenciaId(data.getLicenciaId());
    } else if (det.getLicenciaId() == null || det.getLicenciaId().isBlank()) {
      asignarLicenciaDisponible(det);
    }

    var saved = detalleRepo.save(det);
    recalcularTotal(ordenId);
    return saved;
  }

  public void eliminarDetalle(String ordenId, String detalleId) {
    var det = obtenerDetalle(detalleId);
    if (!ordenId.equals(det.getOrdenId())) throw new IllegalArgumentException("Detalle no pertenece a la orden");
    detalleRepo.deleteById(detalleId);
    recalcularTotal(ordenId);
  }

  // ---- Confirmar Orden ----
  public OrdenCompra confirmar(String ordenId) {
    var oc = obtener(ordenId);
    recalcularTotal(ordenId);
    // aquí podrías cambiar estadoId a "CONFIRMADA"
    return oc;
  }

  public OrdenResumenDto construirResumen(String ordenId) {
    var oc = obtener(ordenId);
    var detalles = detalleRepo.findByOrdenId(ordenId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

    UsuarioClient.UsuarioResponse usuarioResponse = null;
    if (oc.getUsuarioId() != null && !oc.getUsuarioId().isBlank()) {
      usuarioResponse = safeUsuario(oc.getUsuarioId());
    }

    List<OrdenResumenDto.DetalleResumenDto> detalleDtos = detalles.stream().map(det -> {
      var juego = safeJuego(det.getJuegoId());
      var promedio = safePromedio(det.getJuegoId());

      LicenciaClient.LicenciaResponse licencia = null;
      if (det.getLicenciaId() != null && !det.getLicenciaId().isBlank()) {
        licencia = safeLicencia(det.getLicenciaId());
      }

      return new OrdenResumenDto.DetalleResumenDto(
          det.getId(),
          det.getJuegoId(),
          juego != null ? juego.nombreJuego() : null,
          juego != null ? juego.estadoId() : null,
          det.getPrecioUnitario(),
          det.getCantidad(),
          det.getSubtotal(),
          det.getIva(),
          det.getTotalLinea(),
          promedio,
          det.getLicenciaId(),
          licencia != null ? licencia.clave() : null,
          licencia != null ? licencia.estadoId() : null
      );
    }).toList();

    OrdenResumenDto.Usuario usuarioDto = null;
    if (usuarioResponse != null) {
      usuarioDto = new OrdenResumenDto.Usuario(
          usuarioResponse.id(),
          usuarioResponse.nombreUsuario(),
          usuarioResponse.email(),
          usuarioResponse.telefono(),
          usuarioResponse.rolId(),
          usuarioResponse.estadoId(),
          usuarioResponse.creadoEn());
    }

    return new OrdenResumenDto(
        oc.getId(),
        oc.getEstadoId(),
        oc.getTotal(),
        oc.getCreadoEn(),
        usuarioDto,
        detalleDtos);
  }

  // ---- Reglas de cálculo ----
  private void recalcularTotal(String ordenId) {
    var detalles = detalleRepo.findByOrdenId(ordenId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    var total = detalles.stream()
        .map(Detalle::getTotalLinea)         // (subtotal + iva)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    var oc = obtener(ordenId);
    oc.setTotal(total);
    ordenRepo.save(oc);
  }

  private void aplicarPreciosDesdeCatalogo(Detalle detalle) {
    var juego = safeJuego(detalle.getJuegoId());
    if (juego == null) {
      throw new IllegalArgumentException("Juego no disponible: " + detalle.getJuegoId());
    }
    if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
      throw new IllegalArgumentException("Cantidad inválida para el detalle " + detalle.getId());
    }

    detalle.setPrecioUnitario(juego.precio());
    var subtotal = juego.precio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
    detalle.setSubtotal(subtotal);
    detalle.setIva(calcularIva(subtotal));
  }

  private void asignarLicenciaDisponible(Detalle detalle) {
    List<LicenciaClient.LicenciaResponse> disponibles;
    try {
      disponibles = licenciaClient.licenciasPorJuego(detalle.getJuegoId(), 0, 1);
    } catch (FeignException ex) {
      return; // no impedimos continuar la orden; se puede asignar manualmente
    }
    Optional.ofNullable(disponibles)
        .flatMap(list -> list.stream().findFirst())
        .ifPresent(licencia -> detalle.setLicenciaId(licencia.id()));
  }

  private void validarUsuario(String usuarioId) {
    if (usuarioId == null || usuarioId.isBlank()) return;
    try {
      usuarioClient.getUsuario(usuarioId);
    } catch (FeignException.NotFound ex) {
      throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
    } catch (FeignException ex) {
      throw new IllegalStateException("No se pudo validar usuario " + usuarioId, ex);
    }
  }

  private CatalogoClient.JuegoResponse safeJuego(String juegoId) {
    if (juegoId == null || juegoId.isBlank()) return null;
    try {
      return catalogoClient.getJuego(juegoId);
    } catch (FeignException.NotFound ex) {
      throw new IllegalArgumentException("Juego no encontrado: " + juegoId);
    } catch (FeignException ex) {
      throw new IllegalStateException("Error consultando el catálogo", ex);
    }
  }

  private UsuarioClient.UsuarioResponse safeUsuario(String usuarioId) {
    if (usuarioId == null || usuarioId.isBlank()) return null;
    try {
      return usuarioClient.getUsuario(usuarioId);
    } catch (FeignException.NotFound ex) {
      throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
    } catch (FeignException ex) {
      throw new IllegalStateException("Error consultando el microservicio de usuarios", ex);
    }
  }

  private BigDecimal safePromedio(String juegoId) {
    if (juegoId == null || juegoId.isBlank()) return BigDecimal.ZERO;
    try {
      var promedio = resenaClient.obtenerPromedio(juegoId, true);
      return promedio != null ? promedio : BigDecimal.ZERO;
    } catch (FeignException ex) {
      return BigDecimal.ZERO;
    }
  }

  private LicenciaClient.LicenciaResponse safeLicencia(String licenciaId) {
    if (licenciaId == null || licenciaId.isBlank()) return null;
    try {
      return licenciaClient.obtenerLicencia(licenciaId);
    } catch (FeignException ex) {
      return null;
    }
  }

  private BigDecimal calcularIva(BigDecimal base) {
    return base.multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
  }
}

