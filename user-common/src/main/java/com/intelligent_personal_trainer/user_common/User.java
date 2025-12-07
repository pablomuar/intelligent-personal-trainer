package com.intelligent_personal_trainer.user_common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {

    @Schema(description = "Unique system ID managed by User Service")
    private String userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @Min(value = 0, message = "Age must be positive")
    private Integer age;

    @NotNull(message = "Lifestyle is required")
    private Lifestyle lifestyle;

    @Schema(description = "External data source ID")
    private String dataSourceId;

    @Schema(description = "User ID in the external source")
    private String externalSourceUserId;

    private List<String> diseases;
}
