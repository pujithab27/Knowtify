package com.knowtify.repository;

import com.knowtify.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
    List<Knowledge> findByUserId(Long userId);
    List<Knowledge> findByUserIdOrderByPresentedAtDesc(Long userId);

    @Query("SELECT k FROM Knowledge k WHERE k.user.id = :userId AND k.topic.domain.id = :domainId ORDER BY k.presentedAt DESC")
    List<Knowledge> findByUserIdAndDomainId(@Param("userId") Long userId, @Param("domainId") Long domainId);

    @Query("SELECT COUNT(k) FROM Knowledge k WHERE k.user.id = :userId AND k.isCorrect = true")
    long countCorrectAnswersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(k) FROM Knowledge k WHERE k.user.id = :userId AND k.isAnswered = true")
    long countAnsweredByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(k) FROM Knowledge k WHERE k.user.id = :userId AND k.isCorrect = true AND k.topic.domain.id = :domainId")
    long countCorrectAnswersByUserIdAndDomainId(@Param("userId") Long userId, @Param("domainId") Long domainId);
}
