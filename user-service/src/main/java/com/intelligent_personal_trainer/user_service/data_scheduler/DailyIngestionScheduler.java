package com.intelligent_personal_trainer.user_service.data_scheduler;

import com.intelligent_personal_trainer.user_service.client.DataProcessorClient;
import com.intelligent_personal_trainer.user_service.client.dto.ProcessorTriggerRequest;
import com.intelligent_personal_trainer.user_service.persistence.UserEntity;
import com.intelligent_personal_trainer.user_service.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyIngestionScheduler {

    private final UserRepository userRepository;
    private final DataProcessorClient dataProcessorClient;

    @Scheduled(cron = "${scheduler.daily-ingestion.cron:0 0 1 * * ?}")
    public void triggerDailyDataIngestion() {
        log.info("Starting daily data ingestion trigger job");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<UserEntity> eligibleUsers = userRepository.findByDataSourceIdIsNotNullAndExternalSourceUserIdIsNotNull();

        log.info("Found {} users eligible for data update", eligibleUsers.size());

        for (UserEntity user : eligibleUsers) {
            try {
                LocalDate startDate = user.getLastSyncDate() == null ?
                        yesterday.minusDays(30) :
                        user.getLastSyncDate().plusDays(1);

                if (startDate.isAfter(yesterday)) {
                    log.debug("User {} is already up to date", user.getUserId());
                    continue;
                }

                ProcessorTriggerRequest request = new ProcessorTriggerRequest(
                        user.getUserId(),
                        user.getExternalSourceUserId(),
                        user.getDataSourceId(),
                        startDate,
                        yesterday
                );

                dataProcessorClient.triggerIngestion(request);

                user.setLastSyncDate(yesterday);
                userRepository.save(user);

                log.info("Successfully triggered and updated sync date for user: {}", user.getUserId());

            } catch (Exception e) {
                log.error("Failed to trigger ingestion for user {}: {}", user.getUserId(), e.getMessage());
            }
        }

        log.info("Daily data ingestion job finished");
    }
}
