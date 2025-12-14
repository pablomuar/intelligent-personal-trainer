package com.intelligent_personal_trainer.data_processor_service;

import com.intelligent_personal_trainer.common.constants.KafkaConstants;
import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_processor_service.data_reader.FitnessDataReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataProducerServiceTest {

    @Mock
    private KafkaTemplate<String, FitnessData> kafkaTemplate;

    @Mock
    private List<FitnessDataReader> dataReaderList;

    @Mock
    private FitnessDataReader fitnessDataReader;

    @InjectMocks
    private DataProducerService dataProducerService;

    @Test
    void processAndSendData_shouldSendCorrectDataToKafka() {
        String userId = "testUser";
        String externalSourceUserId = "extUser";
        String sourceId = "testSource";
        LocalDate date = LocalDate.now();

        FitnessData fitnessData = FitnessData.builder()
                .timestamp(Instant.now())
                .userId(userId)
                .averageHeartRate(120.0)
                .build();

        // Use a real list or mock stream behavior?
        // Since dataReaderList is a mocked List, stream() might need mocking, which is hard.
        // It's better to InjectMocks with a real List if possible, but Spring injects a List of beans.
        // Let's try to set the dataReaderList manually if possible or use a different approach.
        // Since we used @InjectMocks, Mockito tries to inject mocks. But injecting a List of mocks is tricky.
        // Let's instantiate DataProducerService manually to control the list.

        DataProducerService service = new DataProducerService(kafkaTemplate, List.of(fitnessDataReader));

        when(fitnessDataReader.supportsSource(sourceId)).thenReturn(true);
        when(fitnessDataReader.readData(sourceId, userId, externalSourceUserId, date)).thenReturn(List.of(fitnessData));

        service.processAndSendData(sourceId, userId, externalSourceUserId, date);

        verify(kafkaTemplate).send(KafkaConstants.FITNESS_DATA_TOPIC, userId, fitnessData);
    }

    @Test
    void processAndSendData_shouldLogWarning_whenNoReaderFound() {
        String userId = "testUser";
        String externalSourceUserId = "extUser";
        String sourceId = "testSource";
        LocalDate date = LocalDate.now();

        DataProducerService service = new DataProducerService(kafkaTemplate, List.of(fitnessDataReader));

        when(fitnessDataReader.supportsSource(sourceId)).thenReturn(false);

        service.processAndSendData(sourceId, userId, externalSourceUserId, date);

        verify(kafkaTemplate, never()).send(any(), any(), any());
        verify(fitnessDataReader, never()).readData(any(), any(), any(), any());
    }

    @Test
    void processAndSendData_shouldLogWarning_whenMultipleReadersFound() {
        String userId = "testUser";
        String externalSourceUserId = "extUser";
        String sourceId = "testSource";
        LocalDate date = LocalDate.now();

        FitnessDataReader reader2 = mock(FitnessDataReader.class);
        DataProducerService service = new DataProducerService(kafkaTemplate, List.of(fitnessDataReader, reader2));

        when(fitnessDataReader.supportsSource(sourceId)).thenReturn(true);
        when(reader2.supportsSource(sourceId)).thenReturn(true);

        service.processAndSendData(sourceId, userId, externalSourceUserId, date);

        verify(kafkaTemplate, never()).send(any(), any(), any());
        verify(fitnessDataReader, never()).readData(any(), any(), any(), any());
    }

    @Test
    void processAndSendData_shouldLogWarning_whenNoDataFound() {
        String userId = "testUser";
        String externalSourceUserId = "extUser";
        String sourceId = "testSource";
        LocalDate date = LocalDate.now();

        DataProducerService service = new DataProducerService(kafkaTemplate, List.of(fitnessDataReader));

        when(fitnessDataReader.supportsSource(sourceId)).thenReturn(true);
        when(fitnessDataReader.readData(sourceId, userId, externalSourceUserId, date)).thenReturn(Collections.emptyList());

        service.processAndSendData(sourceId, userId, externalSourceUserId, date);

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }
}
