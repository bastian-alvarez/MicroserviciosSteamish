package com.gamestore.gamecatalog.service;

import com.gamestore.gamecatalog.dto.CreateRatingRequest;
import com.gamestore.gamecatalog.dto.RatingResponse;
import com.gamestore.gamecatalog.entity.Game;
import com.gamestore.gamecatalog.entity.Rating;
import com.gamestore.gamecatalog.repository.GameRepository;
import com.gamestore.gamecatalog.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
    private final RatingRepository ratingRepository;
    private final GameRepository gameRepository;
    
    @Transactional
    public RatingResponse createOrUpdateRating(CreateRatingRequest request, Long userId) {
        // Verificar que el juego existe
        gameRepository.findById(request.getJuegoId())
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        
        // Verificar si el usuario ya calificó este juego
        Optional<Rating> existingRating = ratingRepository.findByJuegoIdAndUsuarioId(
            request.getJuegoId(), userId);
        
        Rating rating;
        if (existingRating.isPresent()) {
            // Actualizar calificación existente
            rating = existingRating.get();
            rating.setCalificacion(request.getCalificacion());
            log.info("Calificación actualizada: Juego={}, Usuario={}, Calificación={}", 
                request.getJuegoId(), userId, request.getCalificacion());
        } else {
            // Crear nueva calificación
            rating = new Rating();
            rating.setJuegoId(request.getJuegoId());
            rating.setUsuarioId(userId);
            rating.setCalificacion(request.getCalificacion());
            log.info("Calificación creada: Juego={}, Usuario={}, Calificación={}", 
                request.getJuegoId(), userId, request.getCalificacion());
        }
        
        rating = ratingRepository.save(rating);
        
        // Actualizar el promedio de calificaciones del juego
        updateGameAverageRating(request.getJuegoId());
        
        return mapToResponse(rating);
    }
    
    public RatingResponse getRatingByUserAndGame(Long juegoId, Long usuarioId) {
        Rating rating = ratingRepository.findByJuegoIdAndUsuarioId(juegoId, usuarioId)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
        return mapToResponse(rating);
    }
    
    public Optional<RatingResponse> getRatingByUserAndGameOptional(Long juegoId, Long usuarioId) {
        return ratingRepository.findByJuegoIdAndUsuarioId(juegoId, usuarioId)
                .map(this::mapToResponse);
    }
    
    public Double getAverageRating(Long juegoId) {
        Double average = ratingRepository.getAverageRatingByJuegoId(juegoId);
        return average != null ? average : 0.0;
    }
    
    public Long getRatingCount(Long juegoId) {
        return ratingRepository.getRatingCountByJuegoId(juegoId);
    }
    
    @Transactional
    private void updateGameAverageRating(Long juegoId) {
        Double averageRating = getAverageRating(juegoId);
        Long ratingCount = getRatingCount(juegoId);
        
        // El promedio se calcula dinámicamente, no se guarda en la tabla de juegos
        // para mantener la consistencia. Se calcula en tiempo real cuando se solicita.
        log.debug("Promedio de calificaciones actualizado para juego {}: {} ({} calificaciones)", 
            juegoId, averageRating, ratingCount);
    }
    
    private RatingResponse mapToResponse(Rating rating) {
        return new RatingResponse(
            rating.getId(),
            rating.getJuegoId(),
            rating.getUsuarioId(),
            rating.getCalificacion(),
            rating.getCreatedAt(),
            rating.getUpdatedAt()
        );
    }
}




