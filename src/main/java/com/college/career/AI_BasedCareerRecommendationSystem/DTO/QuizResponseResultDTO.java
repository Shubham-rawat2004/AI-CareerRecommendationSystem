package com.college.career.AI_BasedCareerRecommendationSystem.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseResultDTO {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private Integer score;
    private Double percentage;
    private String completedAt;
}

