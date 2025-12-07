package com.intelligent_personal_trainer.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.user_common.Lifestyle;
import com.intelligent_personal_trainer.user_common.User;
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
                .name("John")
                .surname("Doe")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .build();

        // Correction: Lifestyle.ACTIVE was removed/renamed in my previous step correction for MapperTest.
        // I should check Lifestyle again. The previous diff showed I replaced ACTIVE with MODERATELY_ACTIVE.
        // So I should use MODERATELY_ACTIVE here to be safe and consistent.
        userInput.setLifestyle(Lifestyle.MODERATELY_ACTIVE);

        User createdUser = User.builder()
                .userId("generated-id")
                .name("John")
                .surname("Doe")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .build();

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInput)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/generated-id"))
                .andExpect(jsonPath("$.userId").value("generated-id"))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void createUser_shouldReturnBadRequest_whenInputIsInvalid() throws Exception {
        User userInput = User.builder()
                // Name is required, missing it should fail
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
                .name("NewName")
                .surname("Doe")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .build();

        User updatedUser = User.builder()
                .userId("user1")
                .name("NewName")
                .surname("Doe")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .build();

        when(userService.updateUser(eq("user1"), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        User userInput = User.builder()
                .name("NewName")
                .surname("Doe")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .build();

        when(userService.updateUser(eq("user1"), any(User.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(put("/users/user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInput)))
                .andExpect(status().isNotFound());
    }
}
