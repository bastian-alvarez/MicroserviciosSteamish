package com.orden.orden.service;


import com.orden.orden.model.Detalle;
import com.orden.orden.model.OrdenCompra;
import com.orden.orden.repository.DetalleRepository;
import com.orden.orden.repository.OrdenRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrdenService {

  private final OrdenRepository ordenRepo;
  private final DetalleRepository detalleRepo;

  public OrdenService(OrdenRepository ordenRepo, DetalleRepository detalleRepo) {
    this.ordenRepo = ordenRepo;
    this.detalleRepo = detalleRepo;
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
    oc.setTotal(BigDecimal.ZERO);
    return ordenRepo.save(oc);
  }

  public OrdenCompra actualizar(String id, OrdenCompra data) {
    var oc = obtener(id);
    oc.setUsuarioId(data.getUsuarioId());
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
    var saved = detalleRepo.save(d);
    recalcularTotal(ordenId);
    return saved;
  }

  public Detalle actualizarDetalle(String ordenId, String detalleId, Detalle data) {
    var det = obtenerDetalle(detalleId);
    if (!ordenId.equals(det.getOrdenId())) throw new IllegalArgumentException("Detalle no pertenece a la orden");
    det.setSubtotal(data.getSubtotal());
    det.setCantidad(data.getCantidad());
    det.setIva(data.getIva());
    det.setLicenciaId(data.getLicenciaId());
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
}

