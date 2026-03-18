package com.intelligent_personal_trainer.data_processor_service.configuration;

import com.intelligent_personal_trainer.common.constants.KafkaConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    @Value("${kafka.configuration.maxPartitions}")
    private int maxPartitions;

    @Bean
    public NewTopic fitnessDataTopic() {
        return TopicBuilder.name(KafkaConstants.FITNESS_DATA_TOPIC)
                .partitions(maxPartitions)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic fitnessDataErrorTopic() {
        return TopicBuilder.name(KafkaConstants.FITNESS_DATA_ERROR_TOPIC)
                .partitions(maxPartitions)
                .replicas(1)
                .build();
    }
}
