package com.gamestore.gamecatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.gamecatalog.dto.CommentResponse;
import com.gamestore.gamecatalog.dto.CreateCommentRequest;
import com.gamestore.gamecatalog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAllComments_Success() throws Exception {
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getAllComments(false)).thenReturn(comments);

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.commentResponseList").exists());
    }

    @Test
    void testGetAllComments_IncludeHidden() throws Exception {
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getAllComments(true)).thenReturn(comments);

        mockMvc.perform(get("/api/comments")
                .param("includeHidden", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateComment_Success() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setJuegoId(1L);
        request.setUsuarioId(1L);
        request.setContenido("New comment");

        when(commentService.createComment(any(CreateCommentRequest.class), eq(1L), any(String.class)))
                .thenReturn(commentResponse);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetCommentsByGame_Success() throws Exception {
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getCommentsByGame(1L, false)).thenReturn(comments);

        mockMvc.perform(get("/api/comments/game/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.commentResponseList").exists());
    }

    @Test
    void testGetCommentsByUser_Success() throws Exception {
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getCommentsByUser(1L, false)).thenReturn(comments);

        mockMvc.perform(get("/api/comments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.commentResponseList").exists());
    }

    @Test
    void testDeleteComment_Success() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());
    }
}

