package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.dto.CommentResponse;
import com.gamestore.gamecatalog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModeratorController.class)
class ModeratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private WebClient.Builder webClientBuilder;

    private CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        commentResponse = new CommentResponse();
        commentResponse.setId(1L);
        commentResponse.setJuegoId(1L);
        commentResponse.setUsuarioId(1L);
        commentResponse.setContenido("Test comment");
    }

    @Test
    void testGetUserComments_Success() throws Exception {
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getCommentsByUser(1L, true)).thenReturn(comments);

        mockMvc.perform(get("/api/moderator/users/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.commentResponseList").exists());
    }

    @Test
    void testHideComment_Success() throws Exception {
        commentResponse.setIsHidden(true);
        when(commentService.hideComment(1L)).thenReturn(commentResponse);

        mockMvc.perform(post("/api/moderator/comments/1/hide"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testHideComment_NotFound() throws Exception {
        when(commentService.hideComment(999L))
                .thenThrow(new RuntimeException("Comentario no encontrado"));

        mockMvc.perform(post("/api/moderator/comments/999/hide"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testShowComment_Success() throws Exception {
        commentResponse.setIsHidden(false);
        when(commentService.showComment(1L)).thenReturn(commentResponse);

        mockMvc.perform(post("/api/moderator/comments/1/show"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testShowComment_NotFound() throws Exception {
        when(commentService.showComment(999L))
                .thenThrow(new RuntimeException("Comentario no encontrado"));

        mockMvc.perform(post("/api/moderator/comments/999/show"))
                .andExpect(status().isNotFound());
    }
}

