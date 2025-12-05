package com.college.career.AI_BasedCareerRecommendationSystem.Controller;


import com.college.career.AI_BasedCareerRecommendationSystem.DTO.ApiResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.DTO.QuizResponseDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.DTO.QuizResponseResultDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Quiz;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.QuizResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.Service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@Tag(name = "Quiz Management", description = "APIs for quiz management and submission")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping
    @Operation(summary = "Get all quizzes", description = "Retrieve all active quizzes")
    public ResponseEntity<ApiResponse<List<Quiz>>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllActiveQuizzes();
        return ResponseEntity.ok(ApiResponse.success("Quizzes retrieved successfully", quizzes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID", description = "Retrieve specific quiz with questions")
    public ResponseEntity<ApiResponse<Quiz>> getQuizById(@PathVariable Long id) {
        var quiz = quizService.getQuizById(id);
        if (quiz.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Quiz retrieved successfully", quiz.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Quiz not found"));
        }
    }

    @PostMapping("/submit/{userId}/{quizId}")
    @Operation(summary = "Submit quiz response", description = "Submit quiz answers and get results")
    public ResponseEntity<ApiResponse<QuizResponseResultDTO>> submitQuiz(
            @PathVariable Long userId,
            @PathVariable Long quizId,
            @RequestBody QuizResponseDTO responseDTO) {
        try {
            QuizResponse response = quizService.submitQuizResponse(userId, quizId, responseDTO.getAnswers());

            QuizResponseResultDTO resultDTO = new QuizResponseResultDTO(
                    response.getId(),
                    response.getQuiz().getId(),
                    response.getQuiz().getTitle(),
                    response.getScore(),
                    response.getPercentage(),
                    response.getCompletedAt().toString()
            );

            return ResponseEntity.ok(ApiResponse.success("Quiz submitted successfully", resultDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/responses/{userId}")
    @Operation(summary = "Get quiz history", description = "Retrieve all quiz responses for a user")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getUserQuizResponses(@PathVariable Long userId) {
        try {
            List<QuizResponse> responses = quizService.getUserQuizResponses(userId);
            return ResponseEntity.ok(ApiResponse.success("Quiz history retrieved successfully", responses));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
