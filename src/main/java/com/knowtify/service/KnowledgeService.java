package com.knowtify.service;

import com.knowtify.entity.*;
import com.knowtify.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final DomainRepository domainRepository;
    private final UserProgressRepository userProgressRepository;

    /**
     * Get next knowledge card for user - NO REPETITION UNTIL ALL TOPICS COVERED
     */
    public Knowledge getNextKnowledgeCard(Long userId, String domainName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Domain domain = domainRepository.findByName(domainName)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found: " + domainName));

        // Get all topics in domain
        List<Topic> allTopics = topicRepository.findByDomain(domain);

        if (allTopics.isEmpty()) {
            throw new IllegalArgumentException("No topics found for domain: " + domainName);
        }

        // Get topics user has already viewed
        List<UserProgress> userProgress = userProgressRepository.findByUserIdAndDomainId(userId, domain.getId());
        Set<Long> viewedTopicIds = userProgress.stream()
                .map(up -> up.getTopic().getId())
                .collect(Collectors.toSet());

        // Filter out viewed topics
        List<Topic> unviewedTopics = allTopics.stream()
                .filter(t -> !viewedTopicIds.contains(t.getId()))
                .collect(Collectors.toList());

        Topic selectedTopic;
        boolean wasReset = false;

        if (unviewedTopics.isEmpty()) {
            // All topics viewed - reset and start new cycle
            resetDomainProgress(userId, domain.getId());
            selectedTopic = allTopics.get(new Random().nextInt(allTopics.size()));
            wasReset = true;
        } else {
            // Select random from unviewed topics
            selectedTopic = unviewedTopics.get(new Random().nextInt(unviewedTopics.size()));
        }

        // Record view (create new progress entry after reset if needed)
        if (wasReset) {
            // After reset, create a fresh progress entry
            UserProgress newProgress = new UserProgress(null, user, selectedTopic, false, false, 0, null, null, null);
            newProgress.recordView();
            userProgressRepository.save(newProgress);
        } else {
            // Update existing progress or create new
            userProgressRepository.findByUserIdAndTopicId(userId, selectedTopic.getId())
                    .ifPresentOrElse(
                            progress -> {
                                progress.recordView();
                                userProgressRepository.save(progress);
                            },
                            () -> {
                                UserProgress newProgress = new UserProgress(null, user, selectedTopic, false, false, 0, null, null, null);
                                newProgress.recordView();
                                userProgressRepository.save(newProgress);
                            }
                    );
        }

        // Create knowledge card
        Knowledge knowledge = new Knowledge();
        knowledge.setUser(user);
        knowledge.setTopic(selectedTopic);
        knowledge.setQuestion(selectedTopic.getName());
        knowledge.setAnswer(selectedTopic.getExample());
        knowledge.setExplanation(selectedTopic.getDescription());
        knowledge.setFullContent(selectedTopic.getContent());
        knowledge.setDifficultyLevel(selectedTopic.getDifficultyLevel());
        knowledge.setIsAnswered(false);
        knowledge.setIsCorrect(null);

        Knowledge saved = knowledgeRepository.save(knowledge);

        if (wasReset) {
            System.out.println("All topics completed! Resetting cycle for user " + userId + " domain " + domainName);
        }

        return saved;
    }

    /**
     * Get knowledge card by ID
     */
    public Optional<Knowledge> getKnowledgeCardById(Long id) {
        return knowledgeRepository.findById(id);
    }

    /**
     * Get all knowledge cards for user
     */
    public List<Knowledge> getUserKnowledgeHistory(Long userId) {
        return knowledgeRepository.findByUserIdOrderByPresentedAtDesc(userId);
    }

    /**
     * Get knowledge cards by domain
     */
    public List<Knowledge> getKnowledgeCardsByDomain(Long userId, Long domainId) {
        return knowledgeRepository.findByUserIdAndDomainId(userId, domainId);
    }

    /**
     * Answer a knowledge card
     */
    public Knowledge answerKnowledgeCard(Long knowledgeId, String userAnswer, Boolean isCorrect, Integer timeSpentSeconds) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge card not found: " + knowledgeId));

        knowledge.setIsAnswered(true);
        knowledge.setUserAnswer(userAnswer);
        knowledge.setIsCorrect(isCorrect);
        knowledge.setTimeSpentSeconds(timeSpentSeconds);
        knowledge.setAnsweredAt(java.time.LocalDateTime.now());

        if (Boolean.TRUE.equals(isCorrect)) {
            userProgressRepository.findByUserIdAndTopicId(knowledge.getUser().getId(), knowledge.getTopic().getId())
                    .ifPresent(progress -> {
                        progress.markCompleted();
                        userProgressRepository.save(progress);
                    });
        }

        return knowledgeRepository.save(knowledge);
    }

    /**
     * Get user statistics
     */
    public Map<String, Object> getUserStats(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        long totalAnswered = knowledgeRepository.countAnsweredByUserId(userId);
        long totalCorrect = knowledgeRepository.countCorrectAnswersByUserId(userId);

        double accuracy = totalAnswered > 0 ? (double) totalCorrect / totalAnswered * 100 : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCardsAnswered", totalAnswered);
        stats.put("totalCardsCorrect", totalCorrect);
        stats.put("accuracy", Math.round(accuracy * 100.0) / 100.0);
        stats.put("totalCardsViewed", userProgressRepository.countByUserId(userId));
        stats.put("totalCardsCompleted", userProgressRepository.countByUserIdAndIsCompletedTrue(userId));

        return stats;
    }

    /**
     * Get domain statistics
     */
    public Map<String, Object> getDomainStats(Long userId, Long domainId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        domainRepository.findById(domainId)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found: " + domainId));

        long totalTopics = topicRepository.countByDomain(domainRepository.findById(domainId).get());
        long completedTopics = userProgressRepository.countCompletedByUserIdAndDomainId(userId, domainId);
        long correctAnswers = knowledgeRepository.countCorrectAnswersByUserIdAndDomainId(userId, domainId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTopics", totalTopics);
        stats.put("completedTopics", completedTopics);
        stats.put("correctAnswers", correctAnswers);
        stats.put("progress", totalTopics > 0 ? (double) completedTopics / totalTopics * 100 : 0);

        return stats;
    }

    /**
     * Reset progress for a domain (start new cycle)
     */
    private void resetDomainProgress(Long userId, Long domainId) {
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
