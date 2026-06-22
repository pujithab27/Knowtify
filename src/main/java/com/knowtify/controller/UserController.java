package com.knowtify.controller;

import com.knowtify.entity.User;
import com.knowtify.service.UserService;
import com.knowtify.service.UserProgressService;
import com.knowtify.dto.PreferencesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserProgressService userProgressService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<?> updatePreferences(
            @PathVariable Long userId,
            @RequestBody PreferencesRequest request) {
        try {
            User user = userService.updateUserPreferencesFromList(
                    userId,
                    request.getDomains(),
                    request.getNotificationTime(),
                    request.getNotificationFrequency()
            );
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @PutMapping("/{userId}/stats")
    public ResponseEntity<?> updateStats(
            @PathVariable Long userId,
            @RequestParam Integer cardsLearned,
            @RequestParam Integer currentStreak,
            @RequestParam Integer longestStreak) {
        try {
            User user = userService.updateUserStats(userId, cardsLearned, currentStreak, longestStreak);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/card-learned")
    public ResponseEntity<?> incrementCardLearned(@PathVariable Long userId) {
        try {
            User user = userService.incrementCardLearned(userId);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/progress")
    public ResponseEntity<?> getUserProgress(@PathVariable Long userId) {
        try {
            var progress = userProgressService.getUserProgress(userId);
            return ResponseEntity.ok(progress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/completed")
    public ResponseEntity<?> getCompletedTopics(@PathVariable Long userId) {
        try {
            var completed = userProgressService.getCompletedTopics(userId);
            return ResponseEntity.ok(completed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/bookmarked")
    public ResponseEntity<?> getBookmarkedTopics(@PathVariable Long userId) {
        try {
            var bookmarked = userProgressService.getBookmarkedTopics(userId);
            return ResponseEntity.ok(bookmarked);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/topic/{topicId}/bookmark")
    public ResponseEntity<?> toggleBookmark(
            @PathVariable Long userId,
            @PathVariable Long topicId) {
        try {
            var progress = userProgressService.toggleBookmark(userId, topicId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "bookmarked", progress.getIsBookmarked(),
                    "message", progress.getIsBookmarked() ? "Bookmarked!" : "Removed from bookmarks",
                    "progress", progress
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @PostMapping("/{userId}/topic/{topicId}/complete")
    public ResponseEntity<?> completeTopicView(
            @PathVariable Long userId,
            @PathVariable Long topicId) {
        try {
            var progress = userProgressService.completeTopicView(userId, topicId);
            return ResponseEntity.ok(progress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/domain/{domainId}/stats")
    public ResponseEntity<?> getDomainProgress(
            @PathVariable Long userId,
            @PathVariable Long domainId) {
        try {
            var progress = userProgressService.getProgressByDomain(userId, domainId);
            var completed = userProgressService.getCompletedCountByDomain(userId, domainId);
            return ResponseEntity.ok(Map.of(
                    "progress", progress,
                    "completedCount", completed
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}
