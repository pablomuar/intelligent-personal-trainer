package com.intelligent_personal_trainer.data_processor_service.data_reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.common.data.WorkoutData;
import com.intelligent_personal_trainer.data_processor_service.configuration.SourceConfig;
import com.opencsv.CSVReaderHeaderAware;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericCsvReaderService implements FitnessDataReader {
    private final ObjectMapper objectMapper;
    private final SimpleTypeConverter typeConverter = new SimpleTypeConverter();
    private final Map<String, SourceConfig> sourceConfigs;

    @Override
    public boolean supportsSource(String sourceId) {
        return sourceConfigs.containsKey(sourceId);
    }

    @Override
    public List<FitnessData> readData(String sourceId, String userId, LocalDate date) {
        SourceConfig config = sourceConfigs.get(sourceId);
        if (config == null) {
            return new ArrayList<>();
        }

        List<FitnessData> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.dateFormat());
        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new FileReader(config.filePath()))) {

            Map<String, String> row;
            while ((row = reader.readMap()) != null) {
                String csvUserId = row.get(config.mappings().get("userId"));
                String csvDateStr = row.get(config.mappings().get("timestamp"));

                if (csvUserId == null || csvDateStr == null) {
                    continue;
                }

                try {
                    LocalDateTime recordDateTime = parseTimestamp(csvDateStr, formatter);

                    if (csvUserId.equals(userId) && recordDateTime.toLocalDate().equals(date)) {
                        FitnessData data = mapRowToEntity(row, config, recordDateTime, userId);
                        results.add(data);
                    }
                } catch (Exception e) {
                    log.warn("Error filtering row for user {} date {}: {}", userId, date, e.getMessage());
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

        List<WorkoutData> allWorkouts = new ArrayList<>();
        WorkoutData flatWorkout = new WorkoutData();
        boolean hasFlatWorkoutInfo = false;

        for (Map.Entry<String, String> entry : config.mappings().entrySet()) {
            String javaField = entry.getKey();
            String csvColumn = entry.getValue();
            String csvValue = row.get(csvColumn);

            if (csvValue != null && !csvValue.isEmpty() && !javaField.equals("timestamp") && !javaField.equals("userId")) {
                try {
                    if (javaField.equals("workoutDataList")) {
                        List<WorkoutData> jsonWorkouts = parseWorkoutsJson(csvValue);
                        allWorkouts.addAll(jsonWorkouts);

                    } else if (javaField.startsWith("workoutData.")) {
                        String attributeName = javaField.split("\\.")[1];

                        if (attributeName.equals("workoutType")) {
                            flatWorkout.setWorkoutType(csvValue);

                        } else {
                            flatWorkout.addAttribute(attributeName, csvValue);
                        }

                        hasFlatWorkoutInfo = true;

                    } else {
                        setValueOnBuilder(builder, javaField, csvValue);
                    }

                } catch (Exception e) {
                    log.warn("Failed to map field '{}': {}", javaField, e.getMessage());
                }
            }
        }

        if (hasFlatWorkoutInfo) {
            allWorkouts.add(flatWorkout);
        }

        if (!allWorkouts.isEmpty()) {
            builder.workoutDataList(allWorkouts);
        }

        return builder.build();
    }

    private List<WorkoutData> parseWorkoutsJson(String jsonValue) {
        try {
            List<Map<String, String>> rawList = objectMapper.readValue(jsonValue, new TypeReference<>(){});
            List<WorkoutData> result = new ArrayList<>();

            for (Map<String, String> rawMap : rawList) {
                WorkoutData wd = new WorkoutData();
                wd.setAttributes(new HashMap<>());

                if (rawMap.containsKey("workoutType")) {
                    wd.setWorkoutType(String.valueOf(rawMap.get("workoutType")));
                    rawMap.remove("workoutType");
                }

                wd.setAttributes(rawMap);
                result.add(wd);
            }

            return result;

        } catch (Exception e) {
            log.error("Failed to parse embedded workouts JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Inyección de dependencias en el Builder mediante Reflexión
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

    private LocalDateTime parseTimestamp(String dateStr, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(dateStr, formatter);

        } catch (DateTimeParseException e) {
            return LocalDate.parse(dateStr, formatter).atStartOfDay();
        }
    }
}