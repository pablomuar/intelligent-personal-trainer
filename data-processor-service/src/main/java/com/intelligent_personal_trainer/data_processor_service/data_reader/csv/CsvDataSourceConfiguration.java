package com.intelligent_personal_trainer.data_processor_service.data_reader.csv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class CsvDataSourceConfiguration {

    @Value("${datasource.sources-path}")
    private String sourcesPath;

    @Bean
    public Map<String, CsvSourceConfig> sourceConfigs(ObjectMapper objectMapper) throws IOException {
        Resource resource = new FileSystemResource(sourcesPath);

        List<CsvSourceConfig> configs = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<>() {
                }
        );

        return configs.stream()
                .collect(Collectors.toMap(CsvSourceConfig::sourceId, Function.identity()));
    }
}