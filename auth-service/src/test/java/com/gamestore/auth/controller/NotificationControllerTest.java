package com.gamestore.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.auth.dto.CreateNotificationRequest;
import com.gamestore.auth.dto.NotificationResponse;
import com.gamestore.auth.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notificationResponse = new NotificationResponse();
        notificationResponse.setId(1L);
        notificationResponse.setUserId(1L);
        notificationResponse.setTitle("Test Notification");
        notificationResponse.setMessage("Test Message");
        notificationResponse.setIsRead(false);
    }

    @Test
    void testGetAllNotifications_Success() throws Exception {
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        when(notificationService.getAllNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.notificationResponseList").exists());
    }

    @Test
    void testGetAllNotifications_MissingUserId() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUnreadNotifications_Success() throws Exception {
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        when(notificationService.getUnreadNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/unread")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.notificationResponseList").exists());
    }

    @Test
    void testGetUnreadCount_Success() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenReturn(3L);

        mockMvc.perform(get("/api/notifications/unread/count")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3L));
    }

    @Test
    void testCreateNotification_Success() throws Exception {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(1L);
        request.setTitle("New Notification");
        request.setMessage("New Message");
        request.setType("INFO");

        when(notificationService.createNotification(any(CreateNotificationRequest.class)))
                .thenReturn(notificationResponse);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testMarkAsRead_Success() throws Exception {
        notificationResponse.setIsRead(true);
        when(notificationService.markAsRead(1L, 1L)).thenReturn(notificationResponse);

        mockMvc.perform(put("/api/notifications/1/read")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testMarkAsRead_MissingUserId() throws Exception {
        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMarkAllAsRead_Success() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1L);

        mockMvc.perform(put("/api/notifications/read-all")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testDeleteNotification_Success() throws Exception {
        doNothing().when(notificationService).deleteNotification(1L, 1L);

        mockMvc.perform(delete("/api/notifications/1")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testDeleteAllNotifications_Success() throws Exception {
        doNothing().when(notificationService).deleteAllNotifications(1L);

        mockMvc.perform(delete("/api/notifications")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}

