package com.gamestore.library.service;

import com.gamestore.library.dto.AddToLibraryRequest;
import com.gamestore.library.dto.LibraryItemResponse;
import com.gamestore.library.entity.LibraryItem;
import com.gamestore.library.repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final LibraryRepository libraryRepository;
    
    @Transactional
    public LibraryItemResponse addToLibrary(AddToLibraryRequest request) {
        // Verificar si ya existe
        if (libraryRepository.existsByUserIdAndJuegoId(request.getUserId(), request.getJuegoId())) {
            throw new RuntimeException("El juego ya está en la biblioteca");
        }
        
        LibraryItem item = new LibraryItem();
        item.setUserId(request.getUserId());
        item.setJuegoId(request.getJuegoId());
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setDateAdded(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        item.setStatus(request.getStatus());
        item.setGenre(request.getGenre());
        
        item = libraryRepository.save(item);
        return mapToResponse(item);
    }
    
    public List<LibraryItemResponse> getUserLibrary(Long userId) {
        return libraryRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public boolean userOwnsGame(Long userId, String juegoId) {
        return libraryRepository.existsByUserIdAndJuegoId(userId, juegoId);
    }
    
    @Transactional
    public void removeFromLibrary(Long userId, String juegoId) {
        libraryRepository.findByUserIdAndJuegoId(userId, juegoId)
                .ifPresent(libraryRepository::delete);
    }
    
    private LibraryItemResponse mapToResponse(LibraryItem item) {
        return new LibraryItemResponse(
            item.getId(),
            item.getUserId(),
            item.getJuegoId(),
            item.getName(),
            item.getPrice(),
            item.getDateAdded(),
            item.getStatus(),
            item.getGenre()
        );
    }
}

