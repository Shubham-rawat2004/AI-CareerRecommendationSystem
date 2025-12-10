package com.college.career.AI_BasedCareerRecommendationSystem.Entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "skill_quiz_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillQuizResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "completed_at", nullable = false, updatable = false)
    private LocalDateTime completedAt;

    @ElementCollection
    @CollectionTable(name = "skill_quiz_answers", joinColumns = @JoinColumn(name = "response_id"))
    @MapKeyColumn(name = "skill_name")
    @Column(name = "proficiency_level")
    private Map<String, String> skillProficiencies; // skill -> "BEGINNER|INTERMEDIATE|ADVANCED"
}
