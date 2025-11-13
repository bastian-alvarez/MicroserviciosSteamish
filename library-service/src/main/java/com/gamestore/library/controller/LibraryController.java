package com.gamestore.library.controller;

import com.gamestore.library.dto.AddToLibraryRequest;
import com.gamestore.library.dto.LibraryItemResponse;
import com.gamestore.library.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibraryController {
    private final LibraryService libraryService;
    
    @PostMapping
    public ResponseEntity<?> addToLibrary(@Valid @RequestBody AddToLibraryRequest request) {
        try {
            LibraryItemResponse item = libraryService.addToLibrary(request);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LibraryItemResponse>> getUserLibrary(@PathVariable Long userId) {
        return ResponseEntity.ok(libraryService.getUserLibrary(userId));
    }
    
    @GetMapping("/user/{userId}/game/{juegoId}")
    public ResponseEntity<Map<String, Boolean>> userOwnsGame(
            @PathVariable Long userId,
            @PathVariable String juegoId) {
        boolean owns = libraryService.userOwnsGame(userId, juegoId);
        return ResponseEntity.ok(Map.of("owns", owns));
    }
    
    @DeleteMapping("/user/{userId}/game/{juegoId}")
    public ResponseEntity<?> removeFromLibrary(
            @PathVariable Long userId,
            @PathVariable String juegoId) {
        libraryService.removeFromLibrary(userId, juegoId);
        return ResponseEntity.ok(Map.of("message", "Juego eliminado de la biblioteca"));
    }
}

