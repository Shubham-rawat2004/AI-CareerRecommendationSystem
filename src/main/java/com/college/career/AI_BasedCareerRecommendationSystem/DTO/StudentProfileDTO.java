package com.college.career.AI_BasedCareerRecommendationSystem.DTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private Long id;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Academic branch is required")
    private String academicBranch;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double cgpa;

    private String bio;

    private List<String> interests;

    private List<String> skills;
}
