package com.intelligent_personal_trainer.user_service.client;

import com.intelligent_personal_trainer.user_service.client.dto.ProcessorTriggerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "data-processor-service")
public interface DataProcessorClient {

    @PostMapping("/data-processor/trigger")
    void triggerIngestion(@RequestBody ProcessorTriggerRequest request);
}
