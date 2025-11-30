package com.gamestore.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.auth.dto.ChangePasswordRequest;
import com.gamestore.auth.dto.UpdateProfilePhotoRequest;
import com.gamestore.auth.dto.UserResponse;
import com.gamestore.auth.service.AuthService;
import com.gamestore.auth.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");
        userResponse.setName("Test User");
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        when(authService.getCurrentUser(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/me")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetCurrentUser_MissingUserId() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCurrentUser_NotFound() throws Exception {
        when(authService.getCurrentUser(999L))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/users/me")
                .param("userId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProfilePhoto_Success() throws Exception {
        UpdateProfilePhotoRequest request = new UpdateProfilePhotoRequest();
        request.setProfilePhotoUri("http://example.com/photo.jpg");

        when(authService.updateProfilePhoto(eq(1L), any(String.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/api/users/me/photo")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUploadProfilePhoto_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(authService.getCurrentUser(1L)).thenReturn(userResponse);
        when(fileStorageService.storeProfilePhoto(any(), eq(1L)))
                .thenReturn("http://example.com/photo.jpg");
        when(authService.updateProfilePhoto(eq(1L), any(String.class)))
                .thenReturn(userResponse);

        mockMvc.perform(multipart("/api/users/me/photo/upload")
                .file(file)
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUploadProfilePhoto_MissingFile() throws Exception {
        mockMvc.perform(multipart("/api/users/me/photo/upload")
                .param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");

        doNothing().when(authService)
                .changePassword(eq(1L), eq("oldPassword"), eq("newPassword"));

        mockMvc.perform(put("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testChangePassword_InvalidCurrentPassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword");

        doThrow(new RuntimeException("CONTRASENA_ACTUAL_INCORRECTA"))
                .when(authService)
                .changePassword(eq(1L), eq("wrongPassword"), eq("newPassword"));

        mockMvc.perform(put("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChangePassword_UserNotFound() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");

        doThrow(new RuntimeException("Usuario no encontrado"))
                .when(authService)
                .changePassword(eq(999L), any(), any());

        mockMvc.perform(put("/api/users/999/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}

