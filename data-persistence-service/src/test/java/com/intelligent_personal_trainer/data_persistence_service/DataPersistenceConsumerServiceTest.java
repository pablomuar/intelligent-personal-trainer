package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DataPersistenceConsumerServiceTest {

    @Mock
    private FitnessDataPersistenceService fitnessDataPersistenceService;

    @InjectMocks
    private DataPersistenceConsumerService consumerService;

    @Test
    void consume_shouldProcessAndSaveData() {
        FitnessData data = FitnessData.builder()
                .userId("user123")
                .build();

        consumerService.consume(data);

        verify(fitnessDataPersistenceService).processAndSave(data);
    }
}
