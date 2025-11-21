package com.intelligent_personal_trainer.data_processor_service;

import com.intelligent_personal_trainer.common.constants.KafkaConstants;
import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.common.data.WorkoutData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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

            FitnessData data = FitnessData.builder()
                    .userId("user123")
                    .averageHeartRate(75.0)
                    .totalCaloriesBurned(120.0)
                    .totalDistance(80.0)
                    .totalSteps(2000)
                    .workoutDataList(List.of(WorkoutData.builder()
                                    .caloriesBurned(120.0)
                                    .distanceKm(5.0)
                                    .durationMinutes(30)
                                    .workoutType("Running")
                            .build()))
                    .build();
            sendData(data.getUserId(), data);
        }
    }
}