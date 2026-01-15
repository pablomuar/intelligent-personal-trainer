package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_persistence_service.entity.FitnessDataEntity;
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
    public void saveOrUpdate(FitnessData fitnessData) {
        Instant timestamp = fitnessData.getTimestamp();
        LocalDate date = LocalDate.ofInstant(timestamp, UTC_ZONE);

        Instant startOfDay = date.atStartOfDay(UTC_ZONE).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(UTC_ZONE).toInstant().minusNanos(1);

        FitnessDataEntity entityToSave = fitnessDataMapper.toEntity(fitnessData);

        List<FitnessDataEntity> existingList = jpaRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                fitnessData.getUserId(), startOfDay, endOfDay);
        if (!existingList.isEmpty()) {
            FitnessDataEntity existingEntity = existingList.getFirst();
            entityToSave.setId(existingEntity.getId());

            log.debug("Overwriting existing data for user {} on date {}", fitnessData.getUserId(), date);

        } else {
            log.debug("Creating new data for user {} on date {}", fitnessData.getUserId(), date);
        }

        jpaRepository.save(entityToSave);
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
