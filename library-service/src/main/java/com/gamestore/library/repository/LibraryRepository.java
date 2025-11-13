package com.gamestore.library.repository;

import com.gamestore.library.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<LibraryItem, Long> {
    List<LibraryItem> findByUserId(Long userId);
    Optional<LibraryItem> findByUserIdAndJuegoId(Long userId, String juegoId);
    boolean existsByUserIdAndJuegoId(Long userId, String juegoId);
}

