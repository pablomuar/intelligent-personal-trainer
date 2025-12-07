package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DataPersistenceController.class)
class DataPersistenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FitnessDataPersistenceService persistenceService;

    @Test
    void getFitnessData_ShouldReturnOk_WhenDataExists() throws Exception {
        String userId = "user1";
        FitnessData data = FitnessData.builder()
                .userId(userId)
                .timestamp(Instant.now())
                .averageHeartRate(120)
                .build();
        List<FitnessData> dataList = List.of(data);

        when(persistenceService.getFitnessDataByUser(eq(userId), any(), any()))
                .thenReturn(dataList);

        mockMvc.perform(get("/data-persistence/fitness-data/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(userId));
    }

    @Test
    void getFitnessData_ShouldReturnNoContent_WhenNoDataExists() throws Exception {
        String userId = "user1";
        when(persistenceService.getFitnessDataByUser(eq(userId), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/data-persistence/fitness-data/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getFitnessData_ShouldPassDateParameters() throws Exception {
        String userId = "user1";
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 31);

        when(persistenceService.getFitnessDataByUser(eq(userId), eq(from), eq(to)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/data-persistence/fitness-data/{userId}", userId)
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getFitnessData_ShouldReturnBadRequest_WhenDateIsInFuture() throws Exception {
        String userId = "user1";
        LocalDate futureDate = LocalDate.now().plusDays(5);

        mockMvc.perform(get("/data-persistence/fitness-data/{userId}", userId)
                        .param("from", futureDate.toString()))
                .andExpect(status().isBadRequest());
    }
}
