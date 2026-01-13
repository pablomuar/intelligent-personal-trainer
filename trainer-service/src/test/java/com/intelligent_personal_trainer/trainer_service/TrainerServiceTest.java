package com.intelligent_personal_trainer.trainer_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.trainer_service.client.DataPersistenceServiceClient;
import com.intelligent_personal_trainer.trainer_service.client.UserServiceClient;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingPlanResponse;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingRequest;
import com.intelligent_personal_trainer.trainer_service.llm.TrainingPlanGeneratorService;
import com.intelligent_personal_trainer.trainer_service.llm.dto.TrainingPlanLlmResponse;
import com.intelligent_personal_trainer.user_common.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainingPlanGeneratorService trainingPlanGeneratorService;

    @Mock
    private DataPersistenceServiceClient persistenceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    void testCreatePlan_Success() {
        TrainingRequest request = new TrainingRequest();
        request.setUserId("user1");
        request.setDaysHistory(7);
        request.setPrompt("Build muscle");

        User user = User.builder()
                .name("John")
                .age(30)
                .lifestyle(com.intelligent_personal_trainer.user_common.Lifestyle.VERY_ACTIVE)
                .build();

        FitnessData fitnessData = new FitnessData();
        fitnessData.setTimestamp(Instant.now());
        List<FitnessData> history = List.of(fitnessData);

        TrainingPlanLlmResponse llmResponse = new TrainingPlanLlmResponse("Rec", "Analysis", Collections.emptyList(), null);

        when(userServiceClient.getUser("user1")).thenReturn(user);
        when(persistenceClient.getFitnessData(eq("user1"), anyString(), anyString())).thenReturn(history);
        when(trainingPlanGeneratorService.generateTrainingPlan(anyString(), any())).thenReturn(llmResponse);

        TrainingPlanResponse response = trainerService.createPlan(request);

        assertNotNull(response);
        assertEquals("user1", response.getUserId());
        assertEquals(llmResponse, response.getTrainingPlan());
        verify(userServiceClient).getUser("user1");
        verify(persistenceClient).getFitnessData(eq("user1"), anyString(), anyString());
        verify(trainingPlanGeneratorService).generateTrainingPlan(anyString(), any());
    }

    @Test
    void testCreatePlan_UserNotFound() {
        TrainingRequest request = new TrainingRequest();
        request.setUserId("user1");
        request.setDaysHistory(7);

        when(userServiceClient.getUser("user1")).thenReturn(null);
        when(persistenceClient.getFitnessData(eq("user1"), anyString(), anyString())).thenReturn(Collections.emptyList());

        TrainingPlanResponse response = trainerService.createPlan(request);

        assertNull(response);
    }

    @Test
    void testCreatePlan_PersistenceFailure_ShouldFailAssumingLogic() {
        TrainingRequest request = new TrainingRequest();
        request.setUserId("user1");
        request.setDaysHistory(7);

        User user = User.builder().build();
        when(userServiceClient.getUser("user1")).thenReturn(user);
        when(persistenceClient.getFitnessData(eq("user1"), anyString(), anyString())).thenThrow(new RuntimeException("DB Error"));

        TrainingPlanResponse response = trainerService.createPlan(request);

        assertNull(response);
    }
}
