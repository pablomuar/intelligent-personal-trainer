package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.constants.KafkaConstants;
import com.intelligent_personal_trainer.common.data.FitnessData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataPersistenceConsumerService {
    private final FitnessDataPersistenceService fitnessDataPersistenceService;

    @Autowired
    public DataPersistenceConsumerService(FitnessDataPersistenceService fitnessDataPersistenceService) {
        this.fitnessDataPersistenceService = fitnessDataPersistenceService;
    }

    @KafkaListener(topics = KafkaConstants.FITNESS_DATA_TOPIC)
    public void consume(FitnessData data) {
        log.debug("Consuming data of userId \"{}\" from topic {}", data.getUserId(), KafkaConstants.FITNESS_DATA_TOPIC);
        fitnessDataPersistenceService.processAndSave(data);
    }
}
