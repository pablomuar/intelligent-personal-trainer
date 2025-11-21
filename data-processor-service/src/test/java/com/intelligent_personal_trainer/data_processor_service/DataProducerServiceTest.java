package com.intelligent_personal_trainer.data_processor_service;

import com.intelligent_personal_trainer.common.constants.KafkaConstants;
import com.intelligent_personal_trainer.common.data.FitnessData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DataProducerServiceTest {

    @Mock
    private KafkaTemplate<String, FitnessData> kafkaTemplate;

    @InjectMocks
    private DataProducerService dataProducerService;

    @Test
    void sendData_shouldSendCorrectDataToKafka() {
        String userId = "testUser";
        FitnessData fitnessData = FitnessData.builder()
                .userId(userId)
                .averageHeartRate(120.0)
                .build();

        dataProducerService.sendData(userId, fitnessData);

        verify(kafkaTemplate).send(KafkaConstants.FITNESS_DATA_TOPIC, userId, fitnessData);
    }
}
