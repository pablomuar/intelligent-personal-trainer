package com.intelligent_personal_trainer.user_service;

import com.intelligent_personal_trainer.user_common.User;
import com.intelligent_personal_trainer.user_service.persistence.UserEntity;
import com.intelligent_personal_trainer.user_service.persistence.UserMapper;
import com.intelligent_personal_trainer.user_service.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldReturnCreatedUser_whenUserDoesNotExist() {
        User userDto = User.builder().userId("user1").build();
        UserEntity userEntity = new UserEntity();
        UserEntity savedEntity = new UserEntity();
        User savedDto = User.builder().userId("user1").build();

        when(userRepository.existsById("user1")).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedEntity);
        when(userMapper.toDto(savedEntity)).thenReturn(savedDto);

        User result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("user1", result.getUserId());
        verify(userRepository).save(userEntity);
    }

    @Test
    void createUser_shouldThrowException_whenUserExists() {
        User userDto = User.builder().userId("user1").build();
        when(userRepository.existsById("user1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUser_shouldReturnUser_whenUserExists() {
        UserEntity entity = new UserEntity();
        User dto = User.builder().userId("user1").build();

        when(userRepository.findById("user1")).thenReturn(Optional.of(entity));
        when(userMapper.toDto(entity)).thenReturn(dto);

        Optional<User> result = userService.getUser("user1");

        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUserId());
    }

    @Test
    void getUser_shouldReturnEmpty_whenUserDoesNotExist() {
        when(userRepository.findById("user1")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUser("user1");

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_shouldReturnList_whenUsersExist() {
        UserEntity entity = new UserEntity();
        User dto = User.builder().userId("user1").build();

        when(userRepository.findAll()).thenReturn(List.of(entity));
        when(userMapper.toDto(entity)).thenReturn(dto);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenUserExists() {
        User userDto = User.builder().userId("user1").name("NewName").build();
        UserEntity existingEntity = new UserEntity();
        UserEntity savedEntity = new UserEntity();
        User savedDto = User.builder().userId("user1").name("NewName").build();

        when(userRepository.findById("user1")).thenReturn(Optional.of(existingEntity));
        when(userRepository.save(existingEntity)).thenReturn(savedEntity);
        when(userMapper.toDto(savedEntity)).thenReturn(savedDto);

        User result = userService.updateUser("user1", userDto);

        assertNotNull(result);
        assertEquals("NewName", result.getName());
        verify(userMapper).updateEntityFromDto(existingEntity, userDto);
        verify(userRepository).save(existingEntity);
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        User userDto = User.builder().userId("user1").build();
        when(userRepository.findById("user1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser("user1", userDto));
        verify(userRepository, never()).save(any());
    }
}
