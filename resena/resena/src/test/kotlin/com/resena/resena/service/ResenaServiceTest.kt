package com.resena.resena.service

import com.resena.resena.model.Resena
import com.resena.resena.repository.ResenaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional

class ResenaServiceTest {

  private lateinit var repository: ResenaRepository
  private lateinit var service: ResenaService

  @BeforeEach
  fun init() {
    repository = mockk(relaxed = true)
    service = ResenaService(repository)
  }

  @Test
  fun `listar por juego y estado usa repositorio combinado`() {
    val pageable = PageRequest.of(0, 10)
    val page = PageImpl(listOf(Resena()))
    every { repository.findByJuegoIdAndEstadoId("GAME-1", "APROBADA", pageable) } returns page

    val result = service.listar(null, "GAME-1", null, "APROBADA", pageable)

    assertEquals(page, result)
    verify { repository.findByJuegoIdAndEstadoId("GAME-1", "APROBADA", pageable) }
  }

  @Test
  fun `promedioPorJuego calcula correctamente`() {
    val reseña1 = Resena().apply { calificacion = 4 }
    val reseña2 = Resena().apply { calificacion = 5 }
    every {
      repository.findByJuegoId("GAME-2", PageRequest.of(0, Int.MAX_VALUE))
    } returns PageImpl(listOf(reseña1, reseña2))

    val promedio = service.promedioPorJuego("GAME-2", false)

    assertEquals("4.50", promedio.toPlainString())
  }

  @Test
  fun `obtiene reseña y lanza error si no existe`() {
    every { repository.findById("RES-1") } returns Optional.empty()

    assertThrows(IllegalArgumentException::class.java) {
      service.obtener("RES-1")
    }
  }
}


