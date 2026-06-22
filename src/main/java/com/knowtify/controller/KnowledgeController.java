package com.knowtify.controller;

import com.knowtify.entity.Knowledge;
import com.knowtify.service.KnowledgeService;
import com.knowtify.dto.AnswerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @GetMapping("/next")
    public ResponseEntity<?> getNextKnowledgeCard(
            @RequestParam Long userId,
            @RequestParam String domain) {
        try {
            Knowledge card = knowledgeService.getNextKnowledgeCard(userId, domain);
            return ResponseEntity.ok(card);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{knowledgeId}")
    public ResponseEntity<?> getKnowledgeCard(@PathVariable Long knowledgeId) {
        return knowledgeService.getKnowledgeCardById(knowledgeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<?> getUserHistory(@PathVariable Long userId) {
        List<Knowledge> history = knowledgeService.getUserKnowledgeHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user/{userId}/domain/{domainId}")
    public ResponseEntity<?> getCardsByDomain(
            @PathVariable Long userId,
            @PathVariable Long domainId) {
        List<Knowledge> cards = knowledgeService.getKnowledgeCardsByDomain(userId, domainId);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/{knowledgeId}/answer")
    public ResponseEntity<?> answerCard(
            @PathVariable Long knowledgeId,
            @RequestBody AnswerRequest request) {
        try {
            Knowledge card = knowledgeService.answerKnowledgeCard(
                    knowledgeId,
                    request.getUserAnswer(),
                    request.getIsCorrect(),
                    request.getTimeSpentSeconds()
            );
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", request.getIsCorrect() ? "Great job!" : "We'll show this again later",
                    "card", card
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = knowledgeService.getUserStats(userId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/domain/{domainId}/stats")
    public ResponseEntity<?> getDomainStats(
            @PathVariable Long userId,
            @PathVariable Long domainId) {
        try {
            Map<String, Object> stats = knowledgeService.getDomainStats(userId, domainId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
