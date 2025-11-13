package com.gamestore.gamecatalog.repository;

import com.gamestore.gamecatalog.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByActivoTrue();
    List<Game> findByCategoriaId(Long categoriaId);
    List<Game> findByGeneroId(Long generoId);
    List<Game> findByDescuentoGreaterThan(Integer descuento);
    
    @Query("SELECT g FROM Game g WHERE g.activo = true AND g.nombre LIKE %:search%")
    List<Game> searchByName(@Param("search") String search);
    
    @Query("SELECT g FROM Game g WHERE g.activo = true AND g.categoriaId = :categoriaId AND g.descuento > 0")
    List<Game> findWithDiscountByCategory(@Param("categoriaId") Long categoriaId);
}

