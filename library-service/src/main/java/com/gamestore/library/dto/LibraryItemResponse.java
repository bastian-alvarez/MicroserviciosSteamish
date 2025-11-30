package com.gamestore.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item de la biblioteca de juegos de un usuario")
public class LibraryItemResponse {
    @Schema(description = "ID único del item en la biblioteca", example = "1")
    private Long id;
    
    @Schema(description = "ID del usuario propietario", example = "5")
    private Long userId;
    
    @Schema(description = "ID del juego", example = "10")
    private String juegoId;
    
    @Schema(description = "Nombre del juego", example = "Cyberpunk 2077")
    private String name;
    
    @Schema(description = "Precio al que fue comprado el juego", example = "59.99")
    private Double price;
    
    @Schema(description = "Fecha en que se agregó a la biblioteca", example = "2024-01-15T10:30:00")
    private String dateAdded;
    
    @Schema(description = "Estado del juego en la biblioteca", example = "Disponible")
    private String status;
    
    @Schema(description = "Género del juego", example = "RPG")
    private String genre;
}

