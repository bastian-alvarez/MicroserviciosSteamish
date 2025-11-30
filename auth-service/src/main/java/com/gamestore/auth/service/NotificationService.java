package com.gamestore.auth.service;

import com.gamestore.auth.dto.CreateNotificationRequest;
import com.gamestore.auth.dto.NotificationResponse;
import com.gamestore.auth.entity.Notification;
import com.gamestore.auth.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    
    public List<NotificationResponse> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getNotificationType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setIsRead(false);
        
        notification = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(notification);
    }
    
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        // Verificar que la notificación pertenece al usuario
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar esta notificación");
        }
        
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(notification);
    }
    
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        // Verificar que la notificación pertenece al usuario
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta notificación");
        }
        
        notificationRepository.delete(notification);
    }
    
    @Transactional
    public void deleteAllNotifications(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }
}

