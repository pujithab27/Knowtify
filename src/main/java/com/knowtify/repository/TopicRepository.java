package com.knowtify.repository;

import com.knowtify.entity.Topic;
import com.knowtify.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByDomain(Domain domain);
    List<Topic> findByDomainName(String domainName);
    long countByDomain(Domain domain);
}
