package com.knowtify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "topic_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "is_bookmarked")
    private Boolean isBookmarked = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "first_viewed_at")
    private LocalDateTime firstViewedAt;

    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (viewCount == null) {
            viewCount = 0;
        }
    }

    public void recordView() {
        this.viewCount = (this.viewCount != null ? this.viewCount : 0) + 1;
        this.lastViewedAt = LocalDateTime.now();
        if (this.firstViewedAt == null) {
            this.firstViewedAt = LocalDateTime.now();
        }
    }

    public void markCompleted() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
        recordView();
    }
}
