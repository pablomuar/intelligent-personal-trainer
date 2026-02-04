package com.intelligent_personal_trainer.trainer_service.client;

import org.springframework.ai.document.Document;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "rag-service", url = "${services.rag.url}")
public interface RagServiceClient {

    @GetMapping("/rag/search")
    List<Document> search(@RequestParam("query") String query);
}
