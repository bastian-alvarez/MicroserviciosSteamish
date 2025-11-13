package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.dto.GameResponse;
import com.gamestore.gamecatalog.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {
    private final GameService gameService;
    
    @GetMapping
    public ResponseEntity<List<GameResponse>> getAllGames(
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) Long genero,
            @RequestParam(required = false) Boolean descuento,
            @RequestParam(required = false) String search) {
        
        List<GameResponse> games;
        
        if (search != null && !search.isEmpty()) {
            games = gameService.searchGames(search);
        } else if (categoria != null) {
            games = gameService.getGamesByCategory(categoria);
        } else if (descuento != null && descuento) {
            games = gameService.getGamesWithDiscount();
        } else {
            games = gameService.getAllGames();
        }
        
        return ResponseEntity.ok(games);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable Long id) {
        try {
            GameResponse game = gameService.getGameById(id);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer newStock = request.get("stock");
            GameResponse game = gameService.updateStock(id, newStock);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<?> decreaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            GameResponse game = gameService.decreaseStock(id, quantity);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

