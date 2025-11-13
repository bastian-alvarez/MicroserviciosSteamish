package com.gamestore.library.entity;

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
public class LibraryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "juego_id", nullable = false)
    private String juegoId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(name = "date_added", nullable = false)
    private String dateAdded;
    
    @Column(nullable = false)
    private String status = "Disponible";
    
    private String genre = "Acción";
}

