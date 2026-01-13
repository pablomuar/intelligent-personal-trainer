package com.intelligent_personal_trainer.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.user_common.Lifestyle;
import com.intelligent_personal_trainer.user_common.User;
import com.intelligent_personal_trainer.user_service.api.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturnCreated_whenInputIsValid() throws Exception {
        User userInput = User.builder()
                .username("john.doe")
                .password("password")
                .name("John")
                .surname("Doe")
                .age(30)
                .height(180)
                .weight(80)
                .gender("MALE")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .diseases(List.of("Flu"))
                .build();

        java.util.Map<String, Object> userMap = objectMapper.convertValue(userInput, java.util.Map.class);
        userMap.put("password", "password");

        User createdUser = User.builder()
                .userId("generated-id")
                .username("john.doe")
                .name("John")
                .surname("Doe")
                .age(30)
                .height(180)
                .weight(80)
                .gender("MALE")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .diseases(List.of("Flu"))
                .build();

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMap)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("generated-id"))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.diseases[0]").value("Flu"));
    }

    @Test
    void createUser_shouldReturnBadRequest_whenInputIsInvalid() throws Exception {
        User userInput = User.builder()
                .surname("Doe")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_shouldReturnUser_whenUserExists() throws Exception {
        User user = User.builder().userId("user1").name("John").build();
        when(userService.getUser("user1")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void getUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userService.getUser("user1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/user1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnList_whenUsersExist() throws Exception {
        User user = User.builder().userId("user1").name("John").build();
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[0].name").value("John"));
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenUserExists() throws Exception {
        User userInput = User.builder()
                .username("john.doe")
                .password("password")
                .name("NewName")
                .surname("Doe")
                .age(30)
                .height(180)
                .weight(80)
                .gender("MALE")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .diseases(List.of("Asthma"))
                .build();

        java.util.Map<String, Object> userMap = objectMapper.convertValue(userInput, java.util.Map.class);
        userMap.put("password", "password");

        User updatedUser = User.builder()
                .userId("user1")
                .username("john.doe")
                .name("NewName")
                .surname("Doe")
                .age(30)
                .height(180)
                .weight(80)
                .gender("MALE")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .diseases(List.of("Asthma"))
                .build();

        when(userService.updateUser(eq("user1"), any(User.class))).thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/users/user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.diseases[0]").value("Asthma"));
    }

    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        User userInput = User.builder()
                .username("john.doe")
                .password("password")
                .name("NewName")
                .surname("Doe")
                .age(30)
                .height(180)
                .weight(80)
                .gender("MALE")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .build();

        java.util.Map<String, Object> userMap = objectMapper.convertValue(userInput, java.util.Map.class);
        userMap.put("password", "password");

        when(userService.updateUser(eq("user1"), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/users/user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMap)))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_shouldReturnOk_whenCredentialsAreValid() throws Exception {
        com.intelligent_personal_trainer.user_service.api.LoginRequest loginRequest = com.intelligent_personal_trainer.user_service.api.LoginRequest.builder()
                .username("john.doe")
                .password("password")
                .build();

        User user = User.builder()
                .userId("user1")
                .username("john.doe")
                .name("John")
                .build();

        when(userService.login("john.doe", "password")).thenReturn(user);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() throws Exception {
        com.intelligent_personal_trainer.user_service.api.LoginRequest loginRequest = com.intelligent_personal_trainer.user_service.api.LoginRequest.builder()
                .username("john.doe")
                .password("wrongpassword")
                .build();

        when(userService.login("john.doe", "wrongpassword")).thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
