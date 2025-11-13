package com.catalogo.catalogo.service

import com.catalogo.catalogo.model.Juego
import com.catalogo.catalogo.repository.JuegoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

class JuegoServiceTest {

  private lateinit var repository: JuegoRepository
  private lateinit var service: JuegoService

  @BeforeEach
  fun setUp() {
    repository = mockk(relaxed = true)
    service = JuegoService(repository)
  }

  @Test
  fun `listar con termino q delega al repositorio`() {
    val pageable = PageRequest.of(0, 10)
    val expected = PageImpl(listOf(Juego()))
    every { repository.findByNombreJuegoContainingIgnoreCase("zelda", pageable) } returns expected

    val result = service.listar("zelda", null, null, null, pageable)

    assertEquals(expected, result)
    verify { repository.findByNombreJuegoContainingIgnoreCase("zelda", pageable) }
  }

  @Test
  fun `actualizar sobrescribe campos del juego`() {
    val original = Juego().apply {
      id = "GAME-1"
      nombreJuego = "Old Name"
      precio = BigDecimal("10.00")
      fotoJuego = "old.png"
      fechaLanzamiento = LocalDate.parse("2020-01-01")
      categoriaId = "CAT-1"
      generoId = "GEN-1"
      estadoId = "ACTIVO"
    }
    val incoming = Juego().apply {
      nombreJuego = "New Name"
      precio = BigDecimal("15.00")
      fotoJuego = "new.png"
      fechaLanzamiento = LocalDate.parse("2021-02-02")
      categoriaId = "CAT-2"
      generoId = "GEN-2"
      estadoId = "INACTIVO"
    }
    every { repository.findById("GAME-1") } returns Optional.of(original)
    every { repository.save(any()) } answers { firstArg() }

    val updated = service.actualizar("GAME-1", incoming)

    assertEquals("New Name", updated.nombreJuego)
    assertEquals(BigDecimal("15.00"), updated.precio)
    assertEquals("CAT-2", updated.categoriaId)
    verify { repository.save(original) }
  }

  @Test
  fun `crear lanza excepcion si id duplicado`() {
    val juego = Juego().apply { id = "GAME-1" }
    every { repository.existsById("GAME-1") } returns true

    assertThrows(IllegalArgumentException::class.java) {
      service.crear(juego)
    }
  }
}




