package com.college.career.AI_BasedCareerRecommendationSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "quiz_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Double percentage;

    // ✅ FIXED: Replace LocalDateTime.now()
    @CreationTimestamp
    @Column(name = "completed_at", nullable = false, updatable = false)
    private LocalDateTime completedAt;

    @ElementCollection
    @CollectionTable(name = "quiz_response_answers", joinColumns = @JoinColumn(name = "response_id"))
    @MapKeyColumn(name = "question_id")
    @Column(name = "selected_option")
    private Map<Long, Integer> answers;
}
