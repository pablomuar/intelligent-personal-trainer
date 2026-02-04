package com.intelligent_personal_trainer.rag_service.controller;

import com.intelligent_personal_trainer.rag_service.service.RagDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagSearchController {

    private final RagDocumentService ragDocumentService;

    @GetMapping("/search")
    public List<Document> search(@RequestParam("query") String query) {
        return ragDocumentService.performSearch(query);
    }
}
