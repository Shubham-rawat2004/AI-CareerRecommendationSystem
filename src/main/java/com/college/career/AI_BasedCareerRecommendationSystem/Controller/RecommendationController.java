package com.college.career.AI_BasedCareerRecommendationSystem.Controller;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.ApiResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.DTO.RecommendationDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@Tag(name = "Career Recommendations", description = "APIs for generating and managing career recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/generate/{userId}")
    @Operation(summary = "Generate recommendations",
            description = "Generate AI-powered career recommendations for a student")
    public ResponseEntity<ApiResponse<List<RecommendationDTO>>> generateRecommendations(
            @PathVariable Long userId) {

        try {
            List<RecommendationDTO> recommendations =
                    recommendationService.generateRecommendations(userId);

            return ResponseEntity.ok(
                    ApiResponse.success("Recommendations generated successfully", recommendations)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/latest-accepted")
    @Operation(summary = "Get latest accepted recommendation",
            description = "Returns the most recently accepted recommendation for the user")
    public ResponseEntity<ApiResponse<RecommendationDTO>> getLatestAccepted(
            @PathVariable Long userId) {

        RecommendationDTO dto =
                recommendationService.getLatestAcceptedRecommendation(userId);

        return ResponseEntity.ok(
                ApiResponse.success("Latest accepted recommendation", dto)
        );
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user recommendations",
            description = "Retrieve all recommendations for a specific user")
    public ResponseEntity<ApiResponse<List<RecommendationDTO>>> getUserRecommendations(
            @PathVariable Long userId) {

        try {
            List<RecommendationDTO> recommendations =
                    recommendationService.getUserRecommendations(userId);

            return ResponseEntity.ok(
                    ApiResponse.success("Recommendations retrieved successfully", recommendations)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{recommendationId}/status")
    @Operation(summary = "Update recommendation status",
            description = "Update status of a recommendation (ACCEPTED/REJECTED)")
    public ResponseEntity<ApiResponse<Void>> updateRecommendationStatus(
            @PathVariable Long recommendationId,
            @RequestParam String status) {

        try {
            recommendationService.updateRecommendationStatus(recommendationId, status);

            return ResponseEntity.ok(
                    ApiResponse.success("Recommendation status updated successfully", null)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
