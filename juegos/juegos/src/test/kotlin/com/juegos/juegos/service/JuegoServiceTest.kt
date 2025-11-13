package com.juegos.juegos.service

import com.juegos.juegos.model.Juego
import com.juegos.juegos.repository.JuegoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
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
  fun init() {
    repository = mockk(relaxed = true)
    service = JuegoService(repository)
  }

  @Test
  fun `listar por genero delega correctamente`() {
    val pageable = PageRequest.of(0, 5)
    val page = PageImpl(listOf(Juego()))
    every { repository.findByGeneroId("RPG", pageable) } returns page

    val result = service.listar(null, null, "RPG", null, pageable)

    assertEquals(page, result)
    verify { repository.findByGeneroId("RPG", pageable) }
  }

  @Test
  fun `obtener devuelve juego existente`() {
    val juego = Juego().apply {
      id = "GAME-10"
      nombreJuego = "Indie"
      precio = BigDecimal("5.00")
      fechaLanzamiento = LocalDate.parse("2023-03-01")
    }
    every { repository.findById("GAME-10") } returns Optional.of(juego)

    val result = service.obtener("GAME-10")

    assertEquals("Indie", result.nombreJuego)
  }
}




