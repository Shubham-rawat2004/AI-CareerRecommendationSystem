package com.college.career.AI_BasedCareerRecommendationSystem.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerDTO {
    private Long id;
    private String name;
    private String description;
    private String requiredSkills;
    private String averageSalary;
    private String jobOutlook;
    private Integer recommendationWeight;
    private Boolean isActive;
}
