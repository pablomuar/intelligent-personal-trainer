package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_persistence_service.entity.FitnessDataEntity;
import com.intelligent_personal_trainer.data_persistence_service.mapper.FitnessDataMapper;
import com.intelligent_personal_trainer.data_persistence_service.repository.FitnessDataJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FitnessDataPersistenceServiceTest {

    @Mock
    private FitnessDataJpaRepository jpaRepository;

    @Mock
    private FitnessDataMapper fitnessDataMapper;

    @InjectMocks
    private FitnessDataPersistenceService persistenceService;

    @Test
    void processAndSave_shouldMapAndSaveEntity() {
        FitnessData dto = FitnessData.builder()
                .timestamp(Instant.now())
                .userId("user123")
                .build();
        FitnessDataEntity entity = new FitnessDataEntity();
        entity.setTimestamp(dto.getTimestamp());
        entity.setUserId("user123");

        when(fitnessDataMapper.toEntity(dto)).thenReturn(entity);

        persistenceService.processAndSave(dto);

        verify(fitnessDataMapper).toEntity(dto);
        verify(jpaRepository).save(entity);
    }

    @Test
    void getFitnessDataByUser_WhenFromIsNull_ShouldCallRepoWithoutDates() {
        String userId = "user1";
        FitnessDataEntity entity = new FitnessDataEntity();
        entity.setUserId(userId);
        List<FitnessDataEntity> entities = List.of(entity);
        FitnessData dto = FitnessData.builder().userId(userId).build();

        when(jpaRepository.findByUserIdOrderByTimestampDesc(userId)).thenReturn(entities);
        when(fitnessDataMapper.toDto(entity)).thenReturn(dto);

        List<FitnessData> result = persistenceService.getFitnessDataByUser(userId, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
        verify(jpaRepository).findByUserIdOrderByTimestampDesc(userId);
    }

    @Test
    void getFitnessDataByUser_WhenFromIsProvided_ShouldCallRepoWithDateRange() {
        String userId = "user1";
        LocalDate from = LocalDate.of(2023, 10, 27);
        LocalDate to = LocalDate.of(2023, 10, 28);

        FitnessDataEntity entity = new FitnessDataEntity();
        entity.setUserId(userId);
        List<FitnessDataEntity> entities = List.of(entity);
        FitnessData dto = FitnessData.builder().userId(userId).build();

        Instant startExpected = from.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant endExpected = to.plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant().minusNanos(1);

        when(jpaRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(eq(userId), eq(startExpected), eq(endExpected)))
                .thenReturn(entities);
        when(fitnessDataMapper.toDto(entity)).thenReturn(dto);

        List<FitnessData> result = persistenceService.getFitnessDataByUser(userId, from, to);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
        verify(jpaRepository).findByUserIdAndTimestampBetweenOrderByTimestampDesc(eq(userId), eq(startExpected), eq(endExpected));
    }

    @Test
    void getFitnessDataByUser_WhenFromIsProvidedAndToIsNull_ShouldUseCurrentTimeAsEnd() {
        String userId = "user1";
        LocalDate from = LocalDate.of(2023, 10, 27);

        FitnessDataEntity entity = new FitnessDataEntity();
        entity.setUserId(userId);
        List<FitnessDataEntity> entities = List.of(entity);
        FitnessData dto = FitnessData.builder().userId(userId).build();

        Instant startExpected = from.atStartOfDay(ZoneId.of("UTC")).toInstant();
        // End time is Instant.now(), so we can't match it exactly. We use any() for end date.

        when(jpaRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(eq(userId), eq(startExpected), any(Instant.class)))
                .thenReturn(entities);
        when(fitnessDataMapper.toDto(entity)).thenReturn(dto);

        List<FitnessData> result = persistenceService.getFitnessDataByUser(userId, from, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
        verify(jpaRepository).findByUserIdAndTimestampBetweenOrderByTimestampDesc(eq(userId), eq(startExpected), any(Instant.class));
    }
}
