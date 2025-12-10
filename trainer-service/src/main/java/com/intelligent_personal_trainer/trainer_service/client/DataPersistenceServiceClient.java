package com.intelligent_personal_trainer.trainer_service.client;

import com.intelligent_personal_trainer.common.data.FitnessData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "persistence-service", url = "${services.persistence.url}")
public interface DataPersistenceServiceClient {

    @GetMapping("/data-persistence/fitness-data/{userId}")
    List<FitnessData> getFitnessData(
            @PathVariable("userId") String userId,
            @RequestParam("from") String from,
            @RequestParam("from") String to
    );
}
