package com.knowtify.controller;

import com.knowtify.entity.Topic;
import com.knowtify.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/domain/{domainId}")
    public ResponseEntity<?> getTopicsByDomain(@PathVariable Long domainId) {
        try {
            List<Topic> topics = topicService.getTopicsByDomain(domainId);
            return ResponseEntity.ok(topics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/domain-name/{domainName}")
    public ResponseEntity<?> getTopicsByDomainName(@PathVariable String domainName) {
        List<Topic> topics = topicService.getTopicsByDomainName(domainName);
        if (topics.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable Long id) {
        return topicService.getTopicById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTopic(
            @RequestParam Long domainId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String content,
            @RequestParam(defaultValue = "BEGINNER") String difficultyLevel,
            @RequestParam(required = false) String keyPoints,
            @RequestParam(required = false) String example) {
        try {
            Topic topic = topicService.createTopic(domainId, name, description, content, difficultyLevel, keyPoints, example);
            return ResponseEntity.ok(topic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTopic(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String content,
            @RequestParam(defaultValue = "BEGINNER") String difficultyLevel,
            @RequestParam(required = false) String keyPoints,
            @RequestParam(required = false) String example) {
        try {
            Topic topic = topicService.updateTopic(id, name, description, content, difficultyLevel, keyPoints, example);
            return ResponseEntity.ok(topic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok("Topic deleted successfully");
    }

    @GetMapping("/{domainId}/count")
    public ResponseEntity<?> getTopicCount(@PathVariable Long domainId) {
        try {
            long count = topicService.getTopicCountByDomain(domainId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
