package com.gamestore.auth.dto;

import com.gamestore.auth.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    @NotNull(message = "userId es requerido")
    private Long userId;
    
    @NotBlank(message = "type es requerido")
    private String type;
    
    @NotBlank(message = "title es requerido")
    private String title;
    
    @NotBlank(message = "message es requerido")
    private String message;
    
    public Notification.NotificationType getNotificationType() {
        try {
            return Notification.NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Notification.NotificationType.OTHER;
        }
    }
}

