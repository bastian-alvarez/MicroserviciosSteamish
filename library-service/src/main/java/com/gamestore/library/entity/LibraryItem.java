package com.gamestore.library.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "biblioteca", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "juego_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un item en la biblioteca de juegos de un usuario")
public class LibraryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del item de biblioteca", example = "1")
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    @Schema(description = "ID del usuario propietario del juego", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
    
    @Column(name = "juego_id", nullable = false)
    @Schema(description = "ID del juego en la biblioteca", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private String juegoId;
    
    @Column(nullable = false)
    @Schema(description = "Nombre del juego", example = "Cyberpunk 2077", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Column(nullable = false)
    @Schema(description = "Precio pagado por el juego", example = "59.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;
    
    @Column(name = "date_added", nullable = false)
    @Schema(description = "Fecha en que el juego fue agregado a la biblioteca", example = "2024-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dateAdded;
    
    @Column(nullable = false)
    @Schema(description = "Estado del juego en la biblioteca", example = "Disponible", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "Disponible")
    private String status = "Disponible";
    
    @Schema(description = "Género del juego", example = "Acción", defaultValue = "Acción")
    private String genre = "Acción";
}

