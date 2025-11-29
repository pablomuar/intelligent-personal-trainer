package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_persistence_service.entity.FitnessDataEntity;
import com.intelligent_personal_trainer.data_persistence_service.mapper.FitnessDataMapper;
import com.intelligent_personal_trainer.data_persistence_service.repository.FitnessDataJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
