package com.intelligent_personal_trainer.data_processor_service.data_reader.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DataSourceConfiguration {

    @Bean
    public Map<String, SourceConfig> sourceConfigs() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClassPathResource resource = new ClassPathResource("sources.json");
        try (InputStream inputStream = resource.getInputStream()) {
            List<SourceConfig> configs = mapper.readValue(
                    inputStream,
                    new TypeReference<>() {}
            );

            return configs.stream()
                    .collect(Collectors.toMap(SourceConfig::sourceId, Function.identity()));
        }
    }
}