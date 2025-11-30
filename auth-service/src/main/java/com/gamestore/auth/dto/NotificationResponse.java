package com.gamestore.auth.dto;

import com.gamestore.auth.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    
    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getUserId(),
            notification.getType() != null ? notification.getType().name() : null,
            notification.getTitle(),
            notification.getMessage(),
            notification.getIsRead(),
            notification.getCreatedAt()
        );
    }
}

