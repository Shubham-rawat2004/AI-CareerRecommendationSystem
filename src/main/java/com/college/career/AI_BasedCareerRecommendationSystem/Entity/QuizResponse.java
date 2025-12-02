package com.college.career.AI_BasedCareerRecommendationSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false)
    private LocalDateTime completedAt = LocalDateTime.now();

    @ElementCollection // tells that field in not an entity
    @CollectionTable(name = "quiz_response_answers", joinColumns = @JoinColumn(name = "response_id"))
    @MapKeyColumn(name = "question_id") // key in map
    @Column(name = "selected_option")  // value in map
    private Map<Long, Integer> answers;
}
