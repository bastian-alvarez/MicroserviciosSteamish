package com.gamestore.auth.service;

import com.gamestore.auth.dto.CreateNotificationRequest;
import com.gamestore.auth.dto.NotificationResponse;
import com.gamestore.auth.entity.Notification;
import com.gamestore.auth.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;
    private CreateNotificationRequest createRequest;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUserId(2L);
        testNotification.setType(Notification.NotificationType.PHOTO_UPLOADED);
        testNotification.setTitle("Foto actualizada");
        testNotification.setMessage("Has actualizado tu foto de perfil");
        testNotification.setIsRead(false);
        testNotification.setCreatedAt(LocalDateTime.now());

        createRequest = new CreateNotificationRequest();
        createRequest.setUserId(2L);
        createRequest.setType("PHOTO_UPLOADED");
        createRequest.setTitle("Foto actualizada");
        createRequest.setMessage("Has actualizado tu foto de perfil");
    }

    @Test
    void testGetAllNotifications() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(2L))
                .thenReturn(Arrays.asList(testNotification));

        List<NotificationResponse> result = notificationService.getAllNotifications(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotification.getId(), result.get(0).getId());
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(2L);
    }

    @Test
    void testGetUnreadNotifications() {
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(2L))
                .thenReturn(Arrays.asList(testNotification));

        List<NotificationResponse> result = notificationService.getUnreadNotifications(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsRead());
        verify(notificationRepository).findByUserIdAndIsReadFalseOrderByCreatedAtDesc(2L);
    }

    @Test
    void testGetUnreadCount() {
        when(notificationRepository.countByUserIdAndIsReadFalse(2L)).thenReturn(5L);

        long count = notificationService.getUnreadCount(2L);

        assertEquals(5L, count);
        verify(notificationRepository).countByUserIdAndIsReadFalse(2L);
    }

    @Test
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        NotificationResponse result = notificationService.createNotification(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getUserId(), result.getUserId());
        assertEquals(createRequest.getTitle(), result.getTitle());
        assertFalse(result.getIsRead());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testMarkAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        NotificationResponse result = notificationService.markAsRead(1L, 2L);

        assertNotNull(result);
        assertTrue(result.getIsRead());
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testMarkAsRead_NotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(999L, 2L));
    }

    @Test
    void testMarkAsRead_WrongUser() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> notificationService.markAsRead(1L, 999L));
        
        assertTrue(exception.getMessage().contains("No tienes permiso"));
    }

    @Test
    void testMarkAllAsRead() {
        Notification unread1 = new Notification();
        unread1.setId(1L);
        unread1.setUserId(2L);
        unread1.setIsRead(false);
        
        Notification unread2 = new Notification();
        unread2.setId(2L);
        unread2.setUserId(2L);
        unread2.setIsRead(false);

        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(2L))
                .thenReturn(Arrays.asList(unread1, unread2));
        when(notificationRepository.saveAll(anyList())).thenReturn(Arrays.asList(unread1, unread2));

        notificationService.markAllAsRead(2L);

        verify(notificationRepository).findByUserIdAndIsReadFalseOrderByCreatedAtDesc(2L);
        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void testDeleteNotification_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        doNothing().when(notificationRepository).delete(any(Notification.class));

        notificationService.deleteNotification(1L, 2L);

        verify(notificationRepository).findById(1L);
        verify(notificationRepository).delete(testNotification);
    }

    @Test
    void testDeleteNotification_WrongUser() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        assertThrows(RuntimeException.class, () -> notificationService.deleteNotification(1L, 999L));
        verify(notificationRepository, never()).delete(any());
    }

    @Test
    void testDeleteAllNotifications() {
        doNothing().when(notificationRepository).deleteByUserId(2L);

        notificationService.deleteAllNotifications(2L);

        verify(notificationRepository).deleteByUserId(2L);
    }
}

