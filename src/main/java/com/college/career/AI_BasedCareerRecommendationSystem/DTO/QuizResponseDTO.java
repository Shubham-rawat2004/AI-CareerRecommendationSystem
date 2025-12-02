package com.college.career.AI_BasedCareerRecommendationSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {
    private Long quizId;
    private Map<Long, Integer> answers; // questionId -> selectedOptionIndex
}
