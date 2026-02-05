package com.intelligent_personal_trainer.trainer_service.client;

import com.intelligent_personal_trainer.common.dto.RagDocumentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "rag-service", url = "${services.rag.url}")
public interface RagServiceClient {

    @GetMapping("/rag/search")
    List<RagDocumentResponse> search(@RequestParam("query") String query);
}
