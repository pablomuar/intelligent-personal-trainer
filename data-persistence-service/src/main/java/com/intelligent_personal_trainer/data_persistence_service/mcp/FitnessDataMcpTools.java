package com.intelligent_personal_trainer.data_persistence_service.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_persistence_service.FitnessDataPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FitnessDataMcpTools {

    private final FitnessDataPersistenceService persistenceService;
    private final ObjectMapper objectMapper;

    @McpTool(name = "getFitnessData", description = "Retrieves a user's workout history. Dates must be in YYYY-MM-DD format.")
    public String getFitnessData(
            @McpToolParam(description = "User ID") String userId,
            @McpToolParam(description = "Start date YYYY-MM-DD") String fromDate,
            @McpToolParam(description = "End date YYYY-MM-DD") String toDate
    ) throws JsonProcessingException {
        List<FitnessData> fitnessDataByUser = persistenceService.getFitnessDataByUser(userId, LocalDate.parse(fromDate), LocalDate.parse(toDate));
        return objectMapper.writeValueAsString(fitnessDataByUser);
    }
}