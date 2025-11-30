package com.gamestore.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String profilePhotoUri;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

