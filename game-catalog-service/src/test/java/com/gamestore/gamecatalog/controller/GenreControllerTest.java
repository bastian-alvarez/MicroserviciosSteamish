package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.entity.Genre;
import com.gamestore.gamecatalog.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreRepository genreRepository;

    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = new Genre();
        genre.setId(1L);
        genre.setNombre("Action");
    }

    @Test
    void testGetAllGenres_Success() throws Exception {
        List<Genre> genres = Arrays.asList(genre);
        when(genreRepository.findAll()).thenReturn(genres);

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.genreList").exists())
                .andExpect(jsonPath("$._embedded.genreList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.genreList[0].nombre").value("Action"));
    }
}

