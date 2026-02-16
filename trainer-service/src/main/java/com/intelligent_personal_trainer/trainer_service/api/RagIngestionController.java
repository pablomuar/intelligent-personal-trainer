package com.intelligent_personal_trainer.trainer_service.api;

import com.intelligent_personal_trainer.trainer_service.llm.rag.DocumentIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/trainer/rag")
@RequiredArgsConstructor
public class RagIngestionController {

    private final DocumentIngestionService ingestionService;

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("The file is empty");
        }

        ingestionService.ingestFile(file.getResource());

        return ResponseEntity.ok("Ingestion process started in background for: " + file.getOriginalFilename());
    }
}
