package com.orden.orden.service

import com.orden.orden.client.CatalogoClient
import com.orden.orden.client.LicenciaClient
import com.orden.orden.client.ResenaClient
import com.orden.orden.client.UsuarioClient
import com.orden.orden.model.Detalle
import com.orden.orden.model.OrdenCompra
import com.orden.orden.repository.DetalleRepository
import com.orden.orden.repository.OrdenRepository
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
import java.util.Optional

class OrdenServiceTest {

  private lateinit var ordenRepo: OrdenRepository
  private lateinit var detalleRepo: DetalleRepository
  private lateinit var catalogoClient: CatalogoClient
  private lateinit var usuarioClient: UsuarioClient
  private lateinit var resenaClient: ResenaClient
  private lateinit var licenciaClient: LicenciaClient

  private fun buildService(): OrdenService {
    ordenRepo = mockk(relaxed = true)
    detalleRepo = mockk(relaxed = true)
    catalogoClient = mockk()
    usuarioClient = mockk()
    resenaClient = mockk()
    licenciaClient = mockk()
    return OrdenService(ordenRepo, detalleRepo, catalogoClient, usuarioClient, resenaClient, licenciaClient)
  }

  @Test
  fun `agregarDetalle aplica datos del catalogo y licencia`() {
    val service = buildService()
    val orden = OrdenCompra().apply {
      id = "ORD-1"
      total = BigDecimal.ZERO
    }
    val detalle = Detalle().apply {
      id = "DET-1"
      juegoId = "GAME-1"
      cantidad = 2
    }

    every { ordenRepo.findById("ORD-1") } returns Optional.of(orden)
    every { catalogoClient.getJuego("GAME-1") } returns CatalogoClient.JuegoResponse(
      "GAME-1",
      "Juego Prueba",
      BigDecimal("10.00"),
      null,
      LocalDate.parse("2024-01-01"),
      "CAT-1",
      "GEN-1",
      "ACTIVO"
    )
    every { licenciaClient.licenciasPorJuego("GAME-1", 0, 1) } returns listOf(
      LicenciaClient.LicenciaResponse(
        "LIC-1",
        "ABC-KEY",
        LocalDate.parse("2025-01-01"),
        "DISPONIBLE",
        "GAME-1"
      )
    )

    var savedDetalle: Detalle? = null
    every { detalleRepo.save(any()) } answers {
      savedDetalle = firstArg<Detalle>().apply { if (id == null) id = "DET-1" }
      savedDetalle!!
    }
    every { detalleRepo.findByOrdenId("ORD-1", any<Pageable>()) } answers {
      PageImpl(listOfNotNull(savedDetalle))
    }
    every { ordenRepo.save(any()) } answers { firstArg() }

    val result = service.agregarDetalle("ORD-1", detalle)

    assertEquals(BigDecimal("10.00"), result.precioUnitario)
    assertEquals(BigDecimal("20.00"), result.subtotal)
    assertEquals(BigDecimal("2.40"), result.iva)
    assertEquals("LIC-1", result.licenciaId)
    assertEquals(BigDecimal("22.40"), orden.total)
  }

  @Test
  fun `construirResumen incorpora datos de usuario juego y reseñas`() {
    val service = buildService()
    val orden = OrdenCompra().apply {
      id = "ORD-2"
      estadoId = "PENDIENTE"
      usuarioId = "USR-9"
      total = BigDecimal("99.99")
      creadoEn = Instant.parse("2024-10-10T10:00:00Z")
    }

    val detalle = Detalle().apply {
      id = "DET-9"
      ordenId = "ORD-2"
      juegoId = "GAME-9"
      precioUnitario = BigDecimal("49.99")
      cantidad = 1
      subtotal = BigDecimal("49.99")
      iva = BigDecimal("6.00")
      licenciaId = "LIC-9"
    }

    every { ordenRepo.findById("ORD-2") } returns Optional.of(orden)
    every { detalleRepo.findByOrdenId("ORD-2", any<Pageable>()) } returns PageImpl(listOf(detalle))
    every { usuarioClient.getUsuario("USR-9") } returns UsuarioClient.UsuarioResponse(
      "USR-9",
      "Player One",
      "player@example.com",
      "555-9999",
      "CLIENTE",
      "ACTIVO",
      Instant.parse("2023-01-01T00:00:00Z")
    )
    every { catalogoClient.getJuego("GAME-9") } returns CatalogoClient.JuegoResponse(
      "GAME-9",
      "RPG Deluxe",
      BigDecimal("49.99"),
      null,
      LocalDate.parse("2022-05-05"),
      "CAT",
      "GEN",
      "ACTIVO"
    )
    every { resenaClient.obtenerPromedio("GAME-9", true) } returns BigDecimal("4.25")
    every { licenciaClient.obtenerLicencia("LIC-9") } returns LicenciaClient.LicenciaResponse(
      "LIC-9",
      "XYZ-123",
      LocalDate.parse("2026-12-31"),
      "ASIGNADA",
      "GAME-9"
    )

    val resumen = service.construirResumen("ORD-2")

    assertEquals("ORD-2", resumen.id())
    assertEquals("USR-9", resumen.usuario()?.id())
    assertEquals("Player One", resumen.usuario()?.nombre())
    assertEquals(1, resumen.detalles().size)
    val detalleDto = resumen.detalles().first()
    assertEquals("RPG Deluxe", detalleDto.juegoNombre())
    assertEquals(BigDecimal("4.25"), detalleDto.promedioCalificacion())
    assertEquals("XYZ-123", detalleDto.licenciaClave())
  }

  @Test
  fun `crear orden lanza error cuando usuario no existe`() {
    val service = buildService()
    val request = Request.create(
      Request.HttpMethod.GET,
      "/internal/usuarios/USR-404",
      emptyMap(),
      null,
      StandardCharsets.UTF_8,
      RequestTemplate()
    )
    every { usuarioClient.getUsuario("USR-404") } throws FeignException.NotFound(
      "Not found",
      request,
      null,
      StandardCharsets.UTF_8
    )
    every { ordenRepo.existsById("ORD-x") } returns false

    val orden = OrdenCompra().apply {
      id = "ORD-x"
      usuarioId = "USR-404"
    }

    assertThrows<IllegalArgumentException> {
      service.crear(orden)
    }
  }
}


