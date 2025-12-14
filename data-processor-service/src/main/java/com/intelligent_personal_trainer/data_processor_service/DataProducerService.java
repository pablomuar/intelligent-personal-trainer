package com.intelligent_personal_trainer.data_processor_service;

import com.intelligent_personal_trainer.common.constants.KafkaConstants;
import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_processor_service.data_reader.FitnessDataReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataProducerService {

    private final KafkaTemplate<String, FitnessData> kafkaTemplate;
    private final List<FitnessDataReader> dataReaderList;

    @Async
    public void processAndSendData(String sourceId, String userId, String externalSourceUserId, LocalDate date) {
        log.info("Starting ingestion for User: {} (External: {}), Source: {}, Date: {}", userId, externalSourceUserId, sourceId, date);

        List<FitnessDataReader> fitnessDataReaders = dataReaderList.stream()
                .filter(reader -> reader.supportsSource(sourceId))
                .toList();


        if (fitnessDataReaders.size() != 1) {
            log.warn(fitnessDataReaders.isEmpty()
                    ? "No fitness data reader found for sourceId: {}"
                    : "Multiple fitness data readers found for sourceId: {}", sourceId);
            return;
        }

        List<FitnessData> dataList = fitnessDataReaders.getFirst().readData(sourceId, userId, externalSourceUserId, date);
        if (dataList.isEmpty()) {
            log.warn("No data found for User: {} (External: {}) on Date: {} from Source: {}", userId, externalSourceUserId, date, sourceId);
            return;
        }

        dataList.forEach(data -> {
            log.debug("Sending data for user {} at timestamp {}", data.getUserId(), data.getTimestamp());
            kafkaTemplate.send(KafkaConstants.FITNESS_DATA_TOPIC, data.getUserId(), data);
        });

        log.info("Successfully sent {} records to Kafka.", dataList.size());
    }
}