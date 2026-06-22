package com.knowtify.service;

import com.knowtify.entity.UserProgress;
import com.knowtify.entity.User;
import com.knowtify.entity.Topic;
import com.knowtify.repository.UserProgressRepository;
import com.knowtify.repository.UserRepository;
import com.knowtify.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    public UserProgress recordTopicView(Long userId, Long topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicId));

        Optional<UserProgress> existingProgress = userProgressRepository.findByUserIdAndTopicId(userId, topicId);
        UserProgress progress;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.recordView();
        } else {
            progress = new UserProgress(null, user, topic, false, false, 0, null, null, null);
            progress.recordView();
        }

        return userProgressRepository.save(progress);
    }

    public UserProgress completeTopicView(Long userId, Long topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicId));

        Optional<UserProgress> existingProgress = userProgressRepository.findByUserIdAndTopicId(userId, topicId);
        UserProgress progress;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            progress = new UserProgress(null, user, topic, false, false, 0, null, null, null);
        }

        progress.markCompleted();
        return userProgressRepository.save(progress);
    }

    public UserProgress toggleBookmark(Long userId, Long topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicId));

        Optional<UserProgress> existingProgress = userProgressRepository.findByUserIdAndTopicId(userId, topicId);
        UserProgress progress;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setIsBookmarked(!progress.getIsBookmarked());
        } else {
            progress = new UserProgress(null, user, topic, false, true, 0, null, null, null);
        }

        return userProgressRepository.save(progress);
    }

    public List<UserProgress> getUserProgress(Long userId) {
        return userProgressRepository.findByUserId(userId);
    }

    public List<UserProgress> getCompletedTopics(Long userId) {
        return userProgressRepository.findByUserIdAndIsCompletedTrue(userId);
    }

    public List<UserProgress> getBookmarkedTopics(Long userId) {
        return userProgressRepository.findByUserIdAndIsBookmarkedTrue(userId);
    }

    public List<UserProgress> getProgressByDomain(Long userId, Long domainId) {
        return userProgressRepository.findByUserIdAndDomainId(userId, domainId);
    }

    public long getCompletedCountByDomain(Long userId, Long domainId) {
        return userProgressRepository.countCompletedByUserIdAndDomainId(userId, domainId);
    }

    public long getTotalProgressCount(Long userId) {
        return userProgressRepository.countByUserId(userId);
    }

    public long getTotalCompletedCount(Long userId) {
        return userProgressRepository.countByUserIdAndIsCompletedTrue(userId);
    }

    public void resetProgressForDomain(Long userId, Long domainId) {
        List<UserProgress> progressList = userProgressRepository.findByUserIdAndDomainId(userId, domainId);
        progressList.forEach(progress -> {
            progress.setIsCompleted(false);
            progress.setViewCount(0);
            progress.setFirstViewedAt(null);
            progress.setLastViewedAt(null);
            progress.setCompletedAt(null);
        });
        userProgressRepository.saveAll(progressList);
    }
}
