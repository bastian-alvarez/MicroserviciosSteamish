package com.gamestore.gamecatalog.repository;

import com.gamestore.gamecatalog.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByJuegoIdAndUsuarioId(Long juegoId, Long usuarioId);
    
    @Query("SELECT AVG(r.calificacion) FROM Rating r WHERE r.juegoId = :juegoId")
    Double getAverageRatingByJuegoId(@Param("juegoId") Long juegoId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.juegoId = :juegoId")
    Long getRatingCountByJuegoId(@Param("juegoId") Long juegoId);
    
    boolean existsByJuegoIdAndUsuarioId(Long juegoId, Long usuarioId);
}




