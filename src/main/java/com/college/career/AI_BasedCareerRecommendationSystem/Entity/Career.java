package com.college.career.AI_BasedCareerRecommendationSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "careers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Career {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(name = "average_salary", columnDefinition = "TEXT")
    private String averageSalary;

    @Column(name = "job_outlook", columnDefinition = "TEXT")
    private String jobOutlook;

    @Column(name = "recommendation_weight", nullable = false)
    private Integer recommendationWeight = 1;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

//    Cascade means what happens to child objects (Recommendation) when parent (Career) changes
    // This controls WHEN recommendations are loaded from the database.

    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recommendation> recommendations;

}
