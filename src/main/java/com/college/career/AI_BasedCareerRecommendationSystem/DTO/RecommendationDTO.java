package com.college.career.AI_BasedCareerRecommendationSystem.DTO;


import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Recommendation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDTO {
    private Long id;
    private String careerName;
    private Double matchScore;
    private String matchReason;
    private LocalDateTime createdAt;
    private String status;

    public static RecommendationDTO fromEntity(Recommendation recommendation) {
        return RecommendationDTO.builder()
                .id(recommendation.getId())
                .careerName(recommendation.getCareer().getName())
                .matchScore(recommendation.getMatchScore())
                .matchReason(recommendation.getMatchReason())
                .createdAt(recommendation.getCreatedAt())
                .status(recommendation.getStatus().toString())
                .build();
    }
}
