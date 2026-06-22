package com.knowtify.repository;

import com.knowtify.entity.UserProgress;
import com.knowtify.entity.User;
import com.knowtify.entity.Topic;
import com.knowtify.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserIdAndTopicId(Long userId, Long topicId);
    List<UserProgress> findByUserId(Long userId);
    List<UserProgress> findByUserIdAndIsCompletedTrue(Long userId);
    List<UserProgress> findByUserIdAndIsBookmarkedTrue(Long userId);

    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId AND up.topic.domain.id = :domainId")
    List<UserProgress> findByUserIdAndDomainId(@Param("userId") Long userId, @Param("domainId") Long domainId);

    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId AND up.topic.domain.id = :domainId AND up.isCompleted = true")
    List<UserProgress> findCompletedByUserIdAndDomainId(@Param("userId") Long userId, @Param("domainId") Long domainId);

    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user.id = :userId AND up.isCompleted = true AND up.topic.domain.id = :domainId")
    long countCompletedByUserIdAndDomainId(@Param("userId") Long userId, @Param("domainId") Long domainId);

    long countByUserId(Long userId);
    long countByUserIdAndIsCompletedTrue(Long userId);
}
