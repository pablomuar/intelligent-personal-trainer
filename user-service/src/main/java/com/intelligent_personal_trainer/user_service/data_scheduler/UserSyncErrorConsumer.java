package com.intelligent_personal_trainer.user_service.data_scheduler;

import com.intelligent_personal_trainer.common.constants.KafkaConstants;
import com.intelligent_personal_trainer.common.data.FitnessDataProcessingError;
import com.intelligent_personal_trainer.user_service.persistence.UserEntity;
import com.intelligent_personal_trainer.user_service.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncErrorConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = KafkaConstants.FITNESS_DATA_ERROR_TOPIC)
    public void consume(FitnessDataProcessingError error) {
        log.warn("Received processing error for user: {}. Error: {}", error.getUserId(), error.getErrorMessage());

        Optional<UserEntity> userOptional = userRepository.findById(error.getUserId());
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            LocalDate failedDate = error.getFailedDate();

            // Set lastSyncDate to the day BEFORE the failed date so it gets picked up by scheduler again
            LocalDate newLastSyncDate = failedDate.minusDays(1);

            if (user.getLastSyncDate() == null || newLastSyncDate.isBefore(user.getLastSyncDate())) {
                user.setLastSyncDate(newLastSyncDate);
                userRepository.save(user);

                log.info("Rolled back lastSyncDate for user {} to {} due to processing failure on {}",
                        user.getUserId(), newLastSyncDate, failedDate);
            } else {
                log.debug("Ignored rollback for user {} to {} because current lastSyncDate is {} (failure on {})",
                        user.getUserId(), newLastSyncDate, user.getLastSyncDate(), failedDate);
            }
        } else {
            log.error("Received error for non-existent user: {}", error.getUserId());
        }
    }
}
