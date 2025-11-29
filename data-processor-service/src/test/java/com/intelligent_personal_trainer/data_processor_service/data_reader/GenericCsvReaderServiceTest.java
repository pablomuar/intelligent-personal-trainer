package com.intelligent_personal_trainer.data_processor_service.data_reader;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.common.data.WorkoutData;
import com.intelligent_personal_trainer.data_processor_service.data_reader.configuration.SourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GenericCsvReaderServiceTest {

    private GenericCsvReaderService service;

    @Mock
    private Map<String, SourceConfig> sourceConfigs;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GenericCsvReaderService(sourceConfigs);
    }

    private File createCsvFile(String fileName, List<String[]> lines) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        try (com.opencsv.CSVWriter writer = new com.opencsv.CSVWriter(new FileWriter(file))) {
            for (String[] line : lines) {
                writer.writeNext(line);
            }
        }
        return file;
    }

    @Test
    void readData_HappyPath() throws IOException {
        String sourceId = "testSource";
        String userId = "user123";
        LocalDate date = LocalDate.of(2023, 10, 27);
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        String timeZone = "UTC";

        File csvFile = createCsvFile("test.csv", List.of(
            new String[]{"user_id", "timestamp_col", "heart_rate", "steps", "workout_type", "duration"},
            new String[]{userId, "2023-10-27 10:00:00", "120.5", "5000", "Running", "30"}
        ));

        Map<String, String> mappings = Map.of(
            "userId", "user_id",
            "timestamp", "timestamp_col",
            "averageHeartRate", "heart_rate",
            "totalSteps", "steps",
            "workoutData.workoutType", "workout_type",
            "workoutData.durationMinutes", "duration"
        );

        SourceConfig config = new SourceConfig(sourceId, timeZone, csvFile.getAbsolutePath(), dateFormat, mappings);
        when(sourceConfigs.get(sourceId)).thenReturn(config);

        List<FitnessData> result = service.readData(sourceId, userId, date);

        assertEquals(1, result.size());
        FitnessData data = result.get(0);
        assertEquals(userId, data.getUserId());
        assertEquals(120.5, data.getAverageHeartRate());
        assertEquals(5000, data.getTotalSteps());

        LocalDateTime expectedDateTime = LocalDateTime.of(2023, 10, 27, 10, 0, 0);
        assertEquals(expectedDateTime.atZone(ZoneId.of(timeZone)).toInstant(), data.getTimestamp());

        assertNotNull(data.getWorkoutDataList());
        assertEquals(1, data.getWorkoutDataList().size());
        WorkoutData workout = data.getWorkoutDataList().get(0);
        assertEquals("Running", workout.getWorkoutType());
        assertEquals(30, workout.getDurationMinutes());
    }

    @Test
    void readData_FilterByUserIdAndDate() throws IOException {
        String sourceId = "testSource";
        String userId = "targetUser";
        LocalDate date = LocalDate.of(2023, 10, 27);
        String dateFormat = "yyyy-MM-dd HH:mm:ss";

        File csvFile = createCsvFile("test_filter.csv", List.of(
            new String[]{"uid", "ts", "steps"},
            new String[]{"targetUser", "2023-10-27 10:00:00", "100"}, // Match
            new String[]{"otherUser", "2023-10-27 10:00:00", "200"},  // Wrong User
            new String[]{"targetUser", "2023-10-28 10:00:00", "300"}  // Wrong Date
        ));

        Map<String, String> mappings = Map.of(
            "userId", "uid",
            "timestamp", "ts",
            "totalSteps", "steps"
        );

        SourceConfig config = new SourceConfig(sourceId, "UTC", csvFile.getAbsolutePath(), dateFormat, mappings);
        when(sourceConfigs.get(sourceId)).thenReturn(config);

        List<FitnessData> result = service.readData(sourceId, userId, date);

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getTotalSteps());
    }

    @Test
    void readData_UnknownSourceId() {
        String sourceId = "unknown";
        when(sourceConfigs.get(sourceId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
            service.readData(sourceId, "user", LocalDate.now())
        );
    }

    @Test
    void readData_FileNotFound() {
        String sourceId = "testSource";
        SourceConfig config = new SourceConfig(sourceId, "UTC", "non_existent_file.csv", "yyyy-MM-dd", Map.of());
        when(sourceConfigs.get(sourceId)).thenReturn(config);

        // Should handle exception internally and return empty list
        List<FitnessData> result = service.readData(sourceId, "user", LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    void readData_MissingRequiredColumnsInRow() throws IOException {
        String sourceId = "testSource";
        LocalDate date = LocalDate.of(2023, 10, 27);

        File csvFile = createCsvFile("test_missing.csv", List.of(
            new String[]{"uid", "ts"},
            new String[]{"user1", "2023-10-27 10:00:00"},
            new String[]{"user1", null}, // Missing timestamp
            new String[]{null, "2023-10-27 10:00:00"} // Missing userId
        ));

        Map<String, String> mappings = Map.of(
            "userId", "uid",
            "timestamp", "ts"
        );

        SourceConfig config = new SourceConfig(sourceId, "UTC", csvFile.getAbsolutePath(), "yyyy-MM-dd HH:mm:ss", mappings);
        when(sourceConfigs.get(sourceId)).thenReturn(config);

        List<FitnessData> result = service.readData(sourceId, "user1", date);

        assertEquals(1, result.size());
    }

    @Test
    void readData_TypeConversionError() throws IOException {
        String sourceId = "testSource";
        String userId = "user1";
        LocalDate date = LocalDate.of(2023, 10, 27);

        File csvFile = createCsvFile("test_conversion.csv", List.of(
            new String[]{"uid", "ts", "steps"},
            new String[]{userId, "2023-10-27 10:00:00", "not_a_number"}
        ));

        Map<String, String> mappings = Map.of(
            "userId", "uid",
            "timestamp", "ts",
            "totalSteps", "steps"
        );

        SourceConfig config = new SourceConfig(sourceId, "UTC", csvFile.getAbsolutePath(), "yyyy-MM-dd HH:mm:ss", mappings);
        when(sourceConfigs.get(sourceId)).thenReturn(config);

        List<FitnessData> result = service.readData(sourceId, userId, date);

        assertEquals(1, result.size());
        // primitive int default is 0
        assertEquals(0, result.get(0).getTotalSteps());
    }

    @Test
    void readData_NoWorkoutData() throws IOException {
        String sourceId = "testSource";
        String userId = "user1";
        LocalDate date = LocalDate.of(2023, 10, 27);

        File csvFile = createCsvFile("test_no_workout.csv", List.of(
            new String[]{"uid", "ts", "steps"},
            new String[]{userId, "2023-10-27 10:00:00", "1000"}
        ));

        Map<String, String> mappings = Map.of(
            "userId", "uid",
            "timestamp", "ts",
            "totalSteps", "steps"
        );

        SourceConfig config = new SourceConfig(sourceId, "UTC", csvFile.getAbsolutePath(), "yyyy-MM-dd HH:mm:ss", mappings);
        when(sourceConfigs.get(sourceId)).thenReturn(config);

        List<FitnessData> result = service.readData(sourceId, userId, date);

        assertEquals(1, result.size());
        assertNull(result.get(0).getWorkoutDataList());
    }
}
