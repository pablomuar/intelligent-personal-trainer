package com.intelligent_personal_trainer.data_processor_service;

import com.intelligent_personal_trainer.common.constants.KafkaConstants;
import com.intelligent_personal_trainer.common.data.FitnessData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataProducerService implements CommandLineRunner {
    private final KafkaTemplate<String, FitnessData> kafkaTemplate;

    @Autowired
    public DataProducerService(KafkaTemplate<String, FitnessData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendData(String userId, FitnessData data) {
        log.debug("Sending userId \"{}\" data to topic {}", userId, KafkaConstants.FITNESS_DATA_TOPIC);
        kafkaTemplate.send(KafkaConstants.FITNESS_DATA_TOPIC, userId, data);
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            Thread.sleep(5000);

            FitnessData data = new FitnessData();
            data.setUserId("user123");
            data.setAverageHeartRate(75.0);
            data.setTotalCaloriesBurned(120.0);
            data.setTotalDistance(80.0);
            data.setTotalSteps(2000);
            sendData(data.getUserId(), data);
        }
    }
}