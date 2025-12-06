package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_persistence_service.mapper.FitnessDataMapper;
import com.intelligent_personal_trainer.data_persistence_service.repository.FitnessDataJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FitnessDataPersistenceService {
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    private final FitnessDataJpaRepository jpaRepository;
    private final FitnessDataMapper fitnessDataMapper;

    @Transactional
    public void processAndSave(FitnessData fitnessData) {
        jpaRepository.save(fitnessDataMapper.toEntity(fitnessData));
        log.debug("Data of user {} saved successfully", fitnessData.getUserId());
    }

    public List<FitnessData> getFitnessDataByUser(String userId, LocalDate from, LocalDate to) {
        if (from == null) {
            return jpaRepository.findByUserIdOrderByTimestampDesc(userId)
                    .stream()
                    .map(fitnessDataMapper::toDto)
                    .toList();
        }

        Instant startInstant = from.atStartOfDay(UTC_ZONE).toInstant();

        Instant endInstant = (to != null)
                ? to.plusDays(1).atStartOfDay(UTC_ZONE).toInstant().minusNanos(1)
                : Instant.now();

        log.debug("Fetching data for user {} between {} (UTC) and {} (UTC)", userId, startInstant, endInstant);

        return jpaRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(userId, startInstant, endInstant)
                .stream()
                .map(fitnessDataMapper::toDto)
                .toList();
    }
}
