package com.college.career.AI_BasedCareerRecommendationSystem.Controller;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.ApiResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.DTO.StudentProfileDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.StudentProfile;
import com.college.career.AI_BasedCareerRecommendationSystem.Service.StudentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student-profiles")
@Tag(name = "Student Profile Management", description = "APIs for managing student profiles")
public class StudentProfileController {

    @Autowired
    private StudentProfileService profileService;

    @PostMapping("/user/{userId}")
    @Operation(summary = "Create or update student profile", description = "Create a new or update existing student profile")
    public ResponseEntity<ApiResponse<StudentProfile>> createOrUpdateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody StudentProfileDTO profileDTO) {
        try {
            StudentProfile profile = profileService.createOrUpdateProfile(userId, profileDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Profile created/updated successfully", profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get student profile", description = "Retrieve student profile by user ID")
    public ResponseEntity<ApiResponse<StudentProfile>> getProfileByUserId(@PathVariable Long userId) {
        var profile = profileService.getProfileByUserId(userId);
        if (profile.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Profile not found"));
        }
    }

    @PutMapping("/{profileId}")
    @Operation(summary = "Update profile details", description = "Update existing student profile")
    public ResponseEntity<ApiResponse<StudentProfile>> updateProfile(
            @PathVariable Long profileId,
            @Valid @RequestBody StudentProfileDTO profileDTO) {
        try {
            StudentProfile profile = profileService.updateProfile(profileId, profileDTO);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
