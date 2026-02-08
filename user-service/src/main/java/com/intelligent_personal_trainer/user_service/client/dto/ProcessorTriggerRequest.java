package com.intelligent_personal_trainer.user_service.client.dto;

import java.time.LocalDate;

public record ProcessorTriggerRequest(
        String userId,
        String externalSourceUserId,
        String sourceId,
        LocalDate date,
        LocalDate dateTo
) {}
