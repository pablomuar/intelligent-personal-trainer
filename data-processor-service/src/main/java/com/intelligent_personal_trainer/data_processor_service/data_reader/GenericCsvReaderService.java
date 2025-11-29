package com.intelligent_personal_trainer.data_processor_service.data_reader;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.common.data.WorkoutData;
import com.intelligent_personal_trainer.data_processor_service.data_reader.configuration.SourceConfig;
import com.opencsv.CSVReaderHeaderAware;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZoneOffset; // Importante para la conversión
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericCsvReaderService implements FitnessDataReader {
    private final SimpleTypeConverter typeConverter = new SimpleTypeConverter();
    private final Map<String, SourceConfig> sourceConfigs;

    @Override
    public List<FitnessData> readData(String sourceId, String userId, LocalDate date) {
        SourceConfig config = sourceConfigs.get(sourceId);
        if (config == null) {
            throw new IllegalArgumentException("Unknown source: " + sourceId);
        }

        List<FitnessData> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.dateFormat());

        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new FileReader(config.filePath()))) {
            Map<String, String> row;
            while ((row = reader.readMap()) != null) {
                // Pre-filter: Check required identifying columns
                String csvUserId = row.get(config.mappings().get("userId"));
                String csvDateStr = row.get(config.mappings().get("timestamp"));

                if (csvUserId == null || csvDateStr == null) {
                    continue;
                }

                // Filter logic: Same userId and same day
                LocalDateTime recordDateTime = LocalDateTime.parse(csvDateStr, formatter);
                if (csvUserId.equals(userId) && recordDateTime.toLocalDate().equals(date)) {
                    FitnessData data = mapRowToEntity(row, config, recordDateTime, userId);
                    results.add(data);
                }
            }

        } catch (Exception e) {
            log.error("Error reading CSV file for source {}: {}", sourceId, e.getMessage(), e);
        }

        return results;
    }

    private FitnessData mapRowToEntity(Map<String, String> row, SourceConfig config, LocalDateTime dateTime, String userId) {
        FitnessData.FitnessDataBuilder builder = FitnessData.builder();
        builder.timestamp(dateTime.atZone(ZoneId.of(config.timeZone())).toInstant());
        builder.userId(userId);

        WorkoutData.WorkoutDataBuilder workoutBuilder = WorkoutData.builder();
        boolean hasWorkoutInfo = false;

        for (Map.Entry<String, String> entry : config.mappings().entrySet()) {
            String javaField = entry.getKey();
            String csvColumn = entry.getValue();
            String csvValue = row.get(csvColumn);

            if (csvValue != null && !csvValue.isEmpty() && !javaField.equals("timestamp") && !javaField.equals("userId")) {
                try {
                    if (javaField.startsWith("workoutData.")) {
                        String childField = javaField.split("\\.")[1];
                        setValueOnBuilder(workoutBuilder, childField, csvValue);
                        hasWorkoutInfo = true;

                    } else {
                        setValueOnBuilder(builder, javaField, csvValue);
                    }

                } catch (Exception e) {
                    log.warn("Failed to map field '{}' with value '{}': {}", javaField, csvValue, e.getMessage());
                }
            }
        }

        if (hasWorkoutInfo) {
            builder.workoutDataList(List.of(workoutBuilder.build()));
        }

        return builder.build();
    }

    /**
     * Generic method to invoke the Builder method using Reflection.
     * Finds a method named 'fieldName' and converts 'value' to the required type.
     */
    private void setValueOnBuilder(Object builderInstance, String fieldName, String value) {
        Method targetMethod = Arrays.stream(builderInstance.getClass().getMethods())
                .filter(m -> m.getName().equals(fieldName))
                .filter(m -> m.getParameterCount() == 1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Method not found in Builder for field: " + fieldName));

        Class<?> paramType = targetMethod.getParameterTypes()[0];

        Object convertedValue = typeConverter.convertIfNecessary(value, paramType);

        ReflectionUtils.invokeMethod(targetMethod, builderInstance, convertedValue);
    }
}