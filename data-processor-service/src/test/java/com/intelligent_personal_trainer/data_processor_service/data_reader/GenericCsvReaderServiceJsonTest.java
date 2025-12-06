package com.intelligent_personal_trainer.data_processor_service.data_reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.common.data.WorkoutData;
import com.intelligent_personal_trainer.data_processor_service.configuration.SourceConfig;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GenericCsvReaderServiceJsonTest {

    private GenericCsvReaderService service;

    @Mock
    private Map<String, SourceConfig> sourceConfigs;

    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper(); // Use real ObjectMapper for JSON parsing
        service = new GenericCsvReaderService(objectMapper, sourceConfigs);
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
    void readData_WithJsonWorkoutData() throws IOException {
        String sourceId = "testSource";
        String userId = "user123";
        LocalDate date = LocalDate.of(2023, 10, 27);
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        String timeZone = "UTC";

        // JSON string representing a list of workouts
        String jsonWorkouts = "[{\"workoutType\":\"Running\",\"duration\":\"30\",\"distance\":\"5.0\"},{\"workoutType\":\"Cycling\",\"duration\":\"45\",\"distance\":\"15.0\"}]";

        File csvFile = createCsvFile("test_json.csv", List.of(
            new String[]{"user_id", "timestamp_col", "workouts_json"},
            new String[]{userId, "2023-10-27 10:00:00", jsonWorkouts}
        ));

        Map<String, String> mappings = Map.of(
            "userId", "user_id",
            "timestamp", "timestamp_col",
            "workoutDataList", "workouts_json"
        );

        SourceConfig config = new SourceConfig(sourceId, timeZone, csvFile.getAbsolutePath(), dateFormat, mappings);
        when(sourceConfigs.get(sourceId)).thenReturn(config);

        List<FitnessData> result = service.readData(sourceId, userId, date);

        assertEquals(1, result.size());
        FitnessData data = result.get(0);
        assertEquals(userId, data.getUserId());

        assertNotNull(data.getWorkoutDataList());
        assertEquals(2, data.getWorkoutDataList().size());

        WorkoutData workout1 = data.getWorkoutDataList().get(0);
        assertEquals("Running", workout1.getWorkoutType());
        assertEquals("30", workout1.getAttributes().get("duration"));
        assertEquals("5.0", workout1.getAttributes().get("distance"));

        WorkoutData workout2 = data.getWorkoutDataList().get(1);
        assertEquals("Cycling", workout2.getWorkoutType());
        assertEquals("45", workout2.getAttributes().get("duration"));
        assertEquals("15.0", workout2.getAttributes().get("distance"));
    }

    @Test
    void readData_WithInvalidJsonWorkoutData() throws IOException {
        String sourceId = "testSource";
        String userId = "user123";
        LocalDate date = LocalDate.of(2023, 10, 27);
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        String timeZone = "UTC";

        // Invalid JSON string
        String invalidJson = "not a valid json";

        File csvFile = createCsvFile("test_invalid_json.csv", List.of(
            new String[]{"user_id", "timestamp_col", "workouts_json"},
            new String[]{userId, "2023-10-27 10:00:00", invalidJson}
        ));

        Map<String, String> mappings = Map.of(
            "userId", "user_id",
            "timestamp", "timestamp_col",
            "workoutDataList", "workouts_json"
        );

        SourceConfig config = new SourceConfig(sourceId, timeZone, csvFile.getAbsolutePath(), dateFormat, mappings);
        when(sourceConfigs.get(sourceId)).thenReturn(config);

        List<FitnessData> result = service.readData(sourceId, userId, date);

        assertEquals(1, result.size());
        FitnessData data = result.get(0);

        assertNull(data.getWorkoutDataList());
    }
}
