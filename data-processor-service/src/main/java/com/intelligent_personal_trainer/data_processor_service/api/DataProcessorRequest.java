package com.intelligent_personal_trainer.data_processor_service.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record DataProcessorRequest(
        @Schema(description = "ID of the user to process")
        @NotBlank(message = "User ID cannot be empty")
        String userId,

        @Schema(description = "User ID in the external source")
        @NotBlank(message = "External Source User ID cannot be empty")
        String externalSourceUserId,

        @Schema(description = "ID of the data source configured in sources.json")
        @NotBlank(message = "Source ID cannot be empty")
        String sourceId,

        @Schema(description = "Date of the data to extract (YYYY-MM-DD). If using a range, this is the start date.", example = "2025-12-19")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date,

        @Schema(description = "Optional end date for a range (YYYY-MM-DD). If provided, data will be processed from 'date' to 'dateTo' inclusive.", example = "2025-12-20")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate dateTo
) {

}