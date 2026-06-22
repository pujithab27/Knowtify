package com.knowtify.service;

import com.knowtify.entity.Topic;
import com.knowtify.entity.Domain;
import com.knowtify.repository.TopicRepository;
import com.knowtify.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final DomainRepository domainRepository;

    public Topic createTopic(Long domainId, String name, String description, String content, String difficultyLevel, String keyPoints, String example) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found: " + domainId));

        Topic topic = new Topic(name, description, content, domain);
        topic.setDifficultyLevel(difficultyLevel);
        topic.setKeyPoints(keyPoints);
        topic.setExample(example);
        return topicRepository.save(topic);
    }

    public List<Topic> getTopicsByDomain(Long domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found: " + domainId));
        return topicRepository.findByDomain(domain);
    }

    public List<Topic> getTopicsByDomainName(String domainName) {
        return topicRepository.findByDomainName(domainName);
    }

    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }

    public Topic updateTopic(Long id, String name, String description, String content, String difficultyLevel, String keyPoints, String example) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + id));

        topic.setName(name);
        topic.setDescription(description);
        topic.setContent(content);
        topic.setDifficultyLevel(difficultyLevel);
        topic.setKeyPoints(keyPoints);
        topic.setExample(example);
        return topicRepository.save(topic);
    }

    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }

    public long getTopicCountByDomain(Long domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found: " + domainId));
        return topicRepository.countByDomain(domain);
    }
}
