package com.gamestore.gamecatalog.service;

import com.gamestore.gamecatalog.dto.CommentResponse;
import com.gamestore.gamecatalog.dto.CreateCommentRequest;
import com.gamestore.gamecatalog.entity.Comment;
import com.gamestore.gamecatalog.entity.Game;
import com.gamestore.gamecatalog.repository.CommentRepository;
import com.gamestore.gamecatalog.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final GameRepository gameRepository;
    
    @Transactional
    public CommentResponse createComment(CreateCommentRequest request, Long userId, String userName) {
        // Verificar que el juego existe
        gameRepository.findById(request.getJuegoId())
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        
        Comment comment = new Comment();
        comment.setJuegoId(request.getJuegoId());
        comment.setUsuarioId(userId);
        comment.setUsuarioNombre(userName);
        comment.setContenido(request.getContenido());
        comment.setIsHidden(false);
        
        comment = commentRepository.save(comment);
        log.info("Comentario creado: ID={}, Juego={}, Usuario={}", comment.getId(), comment.getJuegoId(), comment.getUsuarioId());
        
        return mapToResponse(comment);
    }
    
    public List<CommentResponse> getCommentsByGame(Long juegoId, boolean includeHidden) {
        List<Comment> comments;
        if (includeHidden) {
            comments = commentRepository.findByJuegoIdOrderByCreatedAtDesc(juegoId);
        } else {
            comments = commentRepository.findByJuegoIdAndIsHiddenFalseOrderByCreatedAtDesc(juegoId);
        }
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<CommentResponse> getCommentsByUser(Long usuarioId, boolean includeHidden) {
        List<Comment> comments;
        if (includeHidden) {
            comments = commentRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId);
        } else {
            comments = commentRepository.findByUsuarioIdAndIsHiddenFalseOrderByCreatedAtDesc(usuarioId);
        }
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CommentResponse hideComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        
        comment.setIsHidden(true);
        comment = commentRepository.save(comment);
        log.info("Comentario ocultado: ID={}", comment.getId());
        
        return mapToResponse(comment);
    }
    
    @Transactional
    public CommentResponse showComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        
        comment.setIsHidden(false);
        comment = commentRepository.save(comment);
        log.info("Comentario mostrado: ID={}", comment.getId());
        
        return mapToResponse(comment);
    }
    
    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        return mapToResponse(comment);
    }
    
    public List<CommentResponse> getAllComments(boolean includeHidden) {
        List<Comment> comments;
        if (includeHidden) {
            comments = commentRepository.findAllByOrderByCreatedAtDesc();
        } else {
            comments = commentRepository.findByIsHiddenFalseOrderByCreatedAtDesc();
        }
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        
        commentRepository.delete(comment);
        log.info("Comentario eliminado: ID={}", commentId);
    }
    
    private CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getJuegoId(),
            comment.getUsuarioId(),
            comment.getUsuarioNombre(),
            comment.getContenido(),
            comment.getIsHidden(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }
}

