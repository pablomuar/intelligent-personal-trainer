package com.intelligent_personal_trainer.user_service.data_scheduler;

import com.intelligent_personal_trainer.common.data.FitnessDataProcessingError;
import com.intelligent_personal_trainer.user_service.persistence.UserEntity;
import com.intelligent_personal_trainer.user_service.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSyncErrorConsumerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSyncErrorConsumer userSyncErrorConsumer;

    @Test
    void consume_shouldUpdateLastSyncDate_whenNewDateIsBeforeCurrentDate() {
        String userId = "testUser";
        LocalDate failedDate = LocalDate.of(2023, 10, 27); // Failure on Friday
        LocalDate expectedSyncDate = LocalDate.of(2023, 10, 26); // Should roll back to Thursday
        LocalDate currentSyncDate = LocalDate.of(2023, 10, 28); // Currently set to Saturday

        FitnessDataProcessingError error = FitnessDataProcessingError.builder()
                .userId(userId)
                .failedDate(failedDate)
                .errorMessage("Error")
                .build();

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setLastSyncDate(currentSyncDate);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userSyncErrorConsumer.consume(error);

        verify(userRepository).save(user);
        assert user.getLastSyncDate().equals(expectedSyncDate);
    }

    @Test
    void consume_shouldNotUpdateLastSyncDate_whenNewDateIsAfterCurrentDate() {
        String userId = "testUser";
        LocalDate failedDate = LocalDate.of(2023, 10, 27); // Failure on Friday
        LocalDate newSyncDateCalculated = LocalDate.of(2023, 10, 26); // Calculates Thursday
        LocalDate currentSyncDate = LocalDate.of(2023, 10, 25); // Currently set to Wednesday (earlier than calculated)

        FitnessDataProcessingError error = FitnessDataProcessingError.builder()
                .userId(userId)
                .failedDate(failedDate)
                .errorMessage("Error")
                .build();

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setLastSyncDate(currentSyncDate);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userSyncErrorConsumer.consume(error);

        verify(userRepository, never()).save(any());
        assert user.getLastSyncDate().equals(currentSyncDate);
    }

    @Test
    void consume_shouldUpdateLastSyncDate_whenCurrentDateIsNull() {
        String userId = "testUser";
        LocalDate failedDate = LocalDate.of(2023, 10, 27);
        LocalDate expectedSyncDate = LocalDate.of(2023, 10, 26);

        FitnessDataProcessingError error = FitnessDataProcessingError.builder()
                .userId(userId)
                .failedDate(failedDate)
                .errorMessage("Error")
                .build();

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setLastSyncDate(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userSyncErrorConsumer.consume(error);

        verify(userRepository).save(user);
        assert user.getLastSyncDate().equals(expectedSyncDate);
    }

    @Test
    void consume_shouldNotUpdate_whenUserDoesNotExist() {
        String userId = "unknownUser";
        LocalDate failedDate = LocalDate.of(2023, 10, 27);

        FitnessDataProcessingError error = FitnessDataProcessingError.builder()
                .userId(userId)
                .failedDate(failedDate)
                .errorMessage("Error")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userSyncErrorConsumer.consume(error);

        verify(userRepository, never()).save(any());
    }
}
