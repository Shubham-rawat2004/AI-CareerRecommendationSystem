package com.college.career.AI_BasedCareerRecommendationSystem.Controller;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.ApiResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.SkillQuizResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.Service.SkillQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/skill-quizzes")
public class SkillQuizController {
    @Autowired
    private SkillQuizService skillQuizService;

    @PostMapping("/submit/{userId}")
    public ResponseEntity<ApiResponse<SkillQuizResponse>> submitSkillQuiz(
            @PathVariable Long userId,
            @RequestBody Map<String, String> skillProficiencies) {
        try {
            SkillQuizResponse response = skillQuizService.submitSkillQuiz(userId, skillProficiencies);
            return ResponseEntity.ok(ApiResponse.success("Skill quiz submitted successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/responses/{userId}")
    public ResponseEntity<ApiResponse<List<SkillQuizResponse>>> getUserSkillResponses(@PathVariable Long userId) {
        List<SkillQuizResponse> responses = skillQuizService.getUserSkillResponses(userId);
        return ResponseEntity.ok(ApiResponse.success("Skill quiz history retrieved", responses));
    }
}

