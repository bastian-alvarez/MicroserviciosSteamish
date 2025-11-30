package com.gamestore.gamecatalog.service;

import com.gamestore.gamecatalog.dto.CommentResponse;
import com.gamestore.gamecatalog.dto.CreateCommentRequest;
import com.gamestore.gamecatalog.entity.Comment;
import com.gamestore.gamecatalog.entity.Game;
import com.gamestore.gamecatalog.repository.CommentRepository;
import com.gamestore.gamecatalog.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private CommentService commentService;

    private CreateCommentRequest createRequest;
    private Game testGame;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        createRequest = new CreateCommentRequest();
        createRequest.setJuegoId(1L);
        createRequest.setContenido("Great game!");
        createRequest.setUsuarioId(2L);
        createRequest.setUsuarioNombre("Test User");

        testGame = new Game();
        testGame.setId(1L);
        testGame.setNombre("Test Game");

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setJuegoId(1L);
        testComment.setUsuarioId(2L);
        testComment.setUsuarioNombre("Test User");
        testComment.setContenido("Great game!");
        testComment.setIsHidden(false);
    }

    @Test
    void testCreateComment_Success() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentResponse response = commentService.createComment(createRequest, 2L, "Test User");

        assertNotNull(response);
        assertEquals(testComment.getContenido(), response.getContenido());
        assertFalse(response.getIsHidden());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testCreateComment_GameNotFound() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        createRequest.setJuegoId(999L);
        assertThrows(RuntimeException.class, () -> commentService.createComment(createRequest, 2L, "Test User"));
    }

    @Test
    void testGetCommentsByGame_IncludeHidden() {
        when(commentRepository.findByJuegoIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(testComment));

        List<CommentResponse> result = commentService.getCommentsByGame(1L, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findByJuegoIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testGetCommentsByGame_ExcludeHidden() {
        when(commentRepository.findByJuegoIdAndIsHiddenFalseOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(testComment));

        List<CommentResponse> result = commentService.getCommentsByGame(1L, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findByJuegoIdAndIsHiddenFalseOrderByCreatedAtDesc(1L);
    }

    @Test
    void testGetCommentsByUser() {
        when(commentRepository.findByUsuarioIdAndIsHiddenFalseOrderByCreatedAtDesc(2L))
                .thenReturn(Arrays.asList(testComment));

        List<CommentResponse> result = commentService.getCommentsByUser(2L, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findByUsuarioIdAndIsHiddenFalseOrderByCreatedAtDesc(2L);
    }

    @Test
    void testHideComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentResponse response = commentService.hideComment(1L);

        assertNotNull(response);
        assertTrue(response.getIsHidden());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testHideComment_NotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.hideComment(999L));
    }

    @Test
    void testShowComment() {
        testComment.setIsHidden(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentResponse response = commentService.showComment(1L);

        assertNotNull(response);
        assertFalse(response.getIsHidden());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testGetAllComments_IncludeHidden() {
        when(commentRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(testComment));

        List<CommentResponse> result = commentService.getAllComments(true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetAllComments_ExcludeHidden() {
        when(commentRepository.findByIsHiddenFalseOrderByCreatedAtDesc()).thenReturn(Arrays.asList(testComment));

        List<CommentResponse> result = commentService.getAllComments(false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findByIsHiddenFalseOrderByCreatedAtDesc();
    }

    @Test
    void testDeleteComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        doNothing().when(commentRepository).delete(testComment);

        commentService.deleteComment(1L);

        verify(commentRepository).findById(1L);
        verify(commentRepository).delete(testComment);
    }

    @Test
    void testDeleteComment_NotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(999L));
        verify(commentRepository, never()).delete(any());
    }
}

