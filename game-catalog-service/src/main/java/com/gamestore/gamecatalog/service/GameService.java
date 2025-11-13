package com.gamestore.gamecatalog.service;

import com.gamestore.gamecatalog.dto.GameResponse;
import com.gamestore.gamecatalog.entity.Game;
import com.gamestore.gamecatalog.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    
    public List<GameResponse> getAllGames() {
        return gameRepository.findByActivoTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public GameResponse getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        return mapToResponse(game);
    }
    
    public List<GameResponse> getGamesByCategory(Long categoriaId) {
        return gameRepository.findByCategoriaId(categoriaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<GameResponse> getGamesWithDiscount() {
        return gameRepository.findByDescuentoGreaterThan(0).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<GameResponse> searchGames(String query) {
        return gameRepository.searchByName(query).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public GameResponse updateStock(Long id, Integer newStock) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        game.setStock(newStock);
        game = gameRepository.save(game);
        return mapToResponse(game);
    }
    
    @Transactional
    public GameResponse decreaseStock(Long id, Integer quantity) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        
        if (game.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        game.setStock(game.getStock() - quantity);
        game = gameRepository.save(game);
        return mapToResponse(game);
    }
    
    private GameResponse mapToResponse(Game game) {
        GameResponse response = new GameResponse();
        response.setId(game.getId());
        response.setNombre(game.getNombre());
        response.setDescripcion(game.getDescripcion());
        response.setPrecio(game.getPrecio());
        response.setStock(game.getStock());
        response.setImagenUrl(game.getImagenUrl() != null ? game.getImagenUrl() : "");
        response.setDesarrollador(game.getDesarrollador());
        response.setFechaLanzamiento(game.getFechaLanzamiento());
        response.setCategoriaId(game.getCategoriaId());
        response.setGeneroId(game.getGeneroId());
        response.setActivo(game.getActivo());
        response.setDescuento(game.getDescuento());
        response.setDiscountedPrice(game.getDiscountedPrice());
        response.setHasDiscount(game.getHasDiscount());
        
        if (game.getCategoria() != null) {
            response.setCategoriaNombre(game.getCategoria().getNombre());
        }
        if (game.getGenero() != null) {
            response.setGeneroNombre(game.getGenero().getNombre());
        }
        
        return response;
    }
}

