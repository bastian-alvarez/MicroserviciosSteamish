package com.gamestore.gamecatalog.repository;

import com.gamestore.gamecatalog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByJuegoIdAndIsHiddenFalseOrderByCreatedAtDesc(Long juegoId);
    
    List<Comment> findByJuegoIdOrderByCreatedAtDesc(Long juegoId);
    
    List<Comment> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    
    List<Comment> findByUsuarioIdAndIsHiddenFalseOrderByCreatedAtDesc(Long usuarioId);
    
    Optional<Comment> findByIdAndJuegoId(Long id, Long juegoId);
    
    long countByJuegoIdAndIsHiddenFalse(Long juegoId);
    
    long countByUsuarioId(Long usuarioId);
    
    // Obtener todos los comentarios visibles ordenados por fecha
    List<Comment> findByIsHiddenFalseOrderByCreatedAtDesc();
    
    // Obtener todos los comentarios ordenados por fecha (incluyendo ocultos)
    List<Comment> findAllByOrderByCreatedAtDesc();
}

