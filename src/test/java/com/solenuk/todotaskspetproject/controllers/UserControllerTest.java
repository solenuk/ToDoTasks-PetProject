package com.solenuk.todotaskspetproject.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseUserDTO;
import com.solenuk.todotaskspetproject.enums.UserRole;
import com.solenuk.todotaskspetproject.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private final Integer defaultId = 1;
    private final String defaultFirstName = "TestFirstName";
    private final String defaultLastName = "TestLastName";
    private final String defaultEmail = "test@gmail.com";
    private final UserRole defaultRole = UserRole.USER_ROLE;

    @Test
    @DisplayName("POST /api/users should return 201 Created and DTO when valid")
    void createUser_ShouldReturn201_WhenRequestIsValid() throws Exception {
        String defaultPassword = "TestPassword";
        CreateUserDTO requestDTO = new CreateUserDTO(
            defaultFirstName, defaultLastName, defaultEmail, defaultPassword, defaultRole
        );
        ResponseUserDTO responseDTO = new ResponseUserDTO(
            defaultId, defaultFirstName, defaultLastName, defaultEmail, defaultRole
        );

        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(defaultId))
            .andExpect(jsonPath("$.email").value(defaultEmail))
            .andExpect(jsonPath("$.firstName").value(defaultFirstName));
    }

    @Test
    @DisplayName("POST /api/users should return 400 Bad Request when validation fails")
    void createUser_ShouldReturn400_WhenValidationFails() throws Exception {
        CreateUserDTO invalidRequest = new CreateUserDTO(
            defaultFirstName, defaultLastName, "", "123", defaultRole
        );

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.password").exists());
    }

    @Test
    @DisplayName("GET /api/users/{id} should return 200 OK and DTO when user exists")
    void getUserById_ShouldReturn200_WhenUserExists() throws Exception {
        ResponseUserDTO responseDTO = new ResponseUserDTO(
            defaultId, defaultFirstName, defaultLastName, defaultEmail, defaultRole
        );

        when(userService.getUserById(defaultId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/{id}", defaultId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(defaultId))
            .andExpect(jsonPath("$.email").value(defaultEmail));
    }

    @Test
    @DisplayName("GET /api/users/{id} should return 404 Not Found when user does not exist")
    void getUserById_ShouldReturn404_WhenUserDoesNotExist() throws Exception {
        Integer missingId = 99;

        when(userService.getUserById(missingId)).thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/{id}", missingId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} should return 204 No Content")
    void deleteUser_ShouldReturn204_WhenSuccessful() throws Exception {

        mockMvc.perform(delete("/api/users/{id}", defaultId))
            .andExpect(status().isNoContent());
    }
}
