package com.gamestore.auth.repository;

import com.gamestore.auth.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setUserId(1L);
        testNotification.setType(Notification.NotificationType.ORDER_CREATED);
        testNotification.setTitle("Test Notification");
        testNotification.setMessage("Test Message");
        testNotification.setIsRead(false);
    }

    @Test
    void testSaveNotification() {
        Notification saved = notificationRepository.save(testNotification);
        
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId());
        assertEquals("Test Notification", saved.getTitle());
    }

    @Test
    void testFindByUserIdOrderByCreatedAtDesc() {
        Notification notification1 = new Notification();
        notification1.setUserId(1L);
        notification1.setType(Notification.NotificationType.ORDER_CREATED);
        notification1.setTitle("First");
        notification1.setMessage("Message 1");
        
        Notification notification2 = new Notification();
        notification2.setUserId(1L);
        notification2.setType(Notification.NotificationType.ORDER_CREATED);
        notification2.setTitle("Second");
        notification2.setMessage("Message 2");
        
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        
        List<Notification> notifications = 
            notificationRepository.findByUserIdOrderByCreatedAtDesc(1L);
        
        assertFalse(notifications.isEmpty());
        assertTrue(notifications.size() >= 2);
    }

    @Test
    void testFindByUserIdAndIsReadFalseOrderByCreatedAtDesc() {
        Notification unreadNotification = new Notification();
        unreadNotification.setUserId(1L);
        unreadNotification.setType(Notification.NotificationType.ORDER_CREATED);
        unreadNotification.setTitle("Unread");
        unreadNotification.setMessage("Message");
        unreadNotification.setIsRead(false);
        
        Notification readNotification = new Notification();
        readNotification.setUserId(1L);
        readNotification.setType(Notification.NotificationType.ORDER_CREATED);
        readNotification.setTitle("Read");
        readNotification.setMessage("Message");
        readNotification.setIsRead(true);
        
        notificationRepository.save(unreadNotification);
        notificationRepository.save(readNotification);
        
        List<Notification> unreadNotifications = 
            notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1L);
        
        assertFalse(unreadNotifications.isEmpty());
        assertTrue(unreadNotifications.stream()
            .allMatch(n -> !n.getIsRead()));
    }

    @Test
    void testCountByUserIdAndIsReadFalse() {
        Notification unread1 = new Notification();
        unread1.setUserId(1L);
        unread1.setType(Notification.NotificationType.ORDER_CREATED);
        unread1.setTitle("Unread 1");
        unread1.setMessage("Message");
        unread1.setIsRead(false);
        
        Notification unread2 = new Notification();
        unread2.setUserId(1L);
        unread2.setType(Notification.NotificationType.ORDER_CREATED);
        unread2.setTitle("Unread 2");
        unread2.setMessage("Message");
        unread2.setIsRead(false);
        
        notificationRepository.save(unread1);
        notificationRepository.save(unread2);
        
        long count = notificationRepository.countByUserIdAndIsReadFalse(1L);
        
        assertTrue(count >= 2);
    }

    @Test
    void testDeleteByUserId() {
        Notification notification = new Notification();
        notification.setUserId(1L);
        notification.setType(Notification.NotificationType.ORDER_CREATED);
        notification.setTitle("Test");
        notification.setMessage("Message");
        
        notificationRepository.save(notification);
        notificationRepository.deleteByUserId(1L);
        
        List<Notification> notifications = 
            notificationRepository.findByUserIdOrderByCreatedAtDesc(1L);
        
        assertTrue(notifications.isEmpty());
    }
}

