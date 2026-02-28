package com.intelligent_personal_trainer.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FitnessDataProcessingError {
    private String userId;
    private LocalDate failedDate;
    private String sourceId;
    private String errorMessage;
}
