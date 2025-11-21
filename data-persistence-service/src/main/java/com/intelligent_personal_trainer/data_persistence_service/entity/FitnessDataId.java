package com.intelligent_personal_trainer.data_persistence_service.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FitnessDataId implements Serializable {
    private Long id;
    private Instant timeStamp;
}