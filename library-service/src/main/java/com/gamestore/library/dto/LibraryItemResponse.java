package com.gamestore.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryItemResponse {
    private Long id;
    private Long userId;
    private String juegoId;
    private String name;
    private Double price;
    private String dateAdded;
    private String status;
    private String genre;
}

