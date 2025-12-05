package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data-persistence")
@RequiredArgsConstructor
public class DataPersistenceController {

    private final FitnessDataPersistenceService persistenceService;

    @GetMapping("/fitness-data/{userId}")
    public ResponseEntity<List<FitnessData>> getFitnessData(@PathVariable String userId) {
        List<FitnessData> data = persistenceService.getFitnessDataByUser(userId);

        if (data.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(data);
    }
}
