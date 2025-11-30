package com.gamestore.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.auth.dto.AdminResponse;
import com.gamestore.auth.dto.UpdateUserRequest;
import com.gamestore.auth.dto.UserResponse;
import com.gamestore.auth.service.AuthService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private AdminResponse adminResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");
        userResponse.setName("Test User");

        adminResponse = new AdminResponse();
        adminResponse.setId(1L);
        adminResponse.setEmail("admin@example.com");
        adminResponse.setName("Admin User");
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(userResponse);
        when(authService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResponseList").exists())
                .andExpect(jsonPath("$._embedded.userResponseList[0].id").value(1L));
    }

    @Test
    void testGetAllUsersSimple_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(userResponse);
        when(authService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users/simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(authService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(authService.getUserById(999L))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/admin/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");

        when(authService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        
        when(authService.updateUser(eq(999L), any(UpdateUserRequest.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(put("/api/admin/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(authService).deleteUser(1L);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testBlockUser_Success() throws Exception {
        when(authService.blockUser(1L)).thenReturn(userResponse);

        mockMvc.perform(post("/api/admin/users/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUnblockUser_Success() throws Exception {
        when(authService.unblockUser(1L)).thenReturn(userResponse);

        mockMvc.perform(post("/api/admin/users/1/unblock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetAllAdmins_Success() throws Exception {
        List<AdminResponse> admins = Arrays.asList(adminResponse);
        when(authService.getAllAdmins()).thenReturn(admins);

        mockMvc.perform(get("/api/admin/users/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.adminResponseList").exists());
    }

    @Test
    void testGetAdminById_Success() throws Exception {
        when(authService.getAdminById(1L)).thenReturn(adminResponse);

        mockMvc.perform(get("/api/admin/users/admins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetAdminById_NotFound() throws Exception {
        when(authService.getAdminById(999L))
                .thenThrow(new RuntimeException("Administrador no encontrado"));

        mockMvc.perform(get("/api/admin/users/admins/999"))
                .andExpect(status().isNotFound());
    }
}

