package com.intelligent_personal_trainer.user_service;

import com.intelligent_personal_trainer.user_common.User;
import com.intelligent_personal_trainer.user_service.persistence.UserEntity;
import com.intelligent_personal_trainer.user_service.persistence.UserMapper;
import com.intelligent_personal_trainer.user_service.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User createUser(User user) {
        log.info("Creating user with ID: {}", user.getUserId());

        if (userRepository.existsById(user.getUserId())) {
            throw new IllegalArgumentException("User with ID " + user.getUserId() + " already exists.");
        }

        UserEntity entity = userMapper.toEntity(user);
        return userMapper.toDto(userRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(String userId) {
        log.debug("Fetching user with ID: {}", userId);
        return userRepository.findById(userId).map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public User updateUser(String userId, User user) {
        user.setUserId(userId);

        log.info("Updating user with ID: {}", userId);

        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        userMapper.updateEntityFromDto(entity, user);
        return userMapper.toDto(userRepository.save(entity));
    }
}