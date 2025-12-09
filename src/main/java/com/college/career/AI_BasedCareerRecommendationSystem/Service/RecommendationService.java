package com.college.career.AI_BasedCareerRecommendationSystem.Service;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.RecommendationDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Career;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Recommendation;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.StudentProfile;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.User;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.CareerRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.RecommendationRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.StudentProfileRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private StudentProfileRepository profileRepository;

    public List<RecommendationDTO> generateRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        List<Career> allCareers = careerRepository.findByIsActiveTrue();
        List<Recommendation> recommendations = new ArrayList<>();

        for (Career career : allCareers) {
            Double matchScore = calculateMatchScore(profile, career);
            String matchReason = generateMatchReason(profile, career, matchScore);

            Recommendation recommendation = Recommendation.builder()
                    .user(user)
                    .career(career)
                    .matchScore(matchScore)
                    .matchReason(matchReason)
                    .status(Recommendation.RecommendationStatus.PENDING)
                    .build();

            recommendations.add(recommendation);
        }

        recommendations = recommendations.stream()
                .sorted((r1, r2) -> r2.getMatchScore().compareTo(r1.getMatchScore()))
                .limit(5)
                .collect(Collectors.toList());

        recommendationRepository.saveAll(recommendations);

        return recommendations.stream()
                .map(RecommendationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private Double calculateMatchScore(StudentProfile profile, Career career) {
        double score = 0.0;

        // CGPA factor (0-30 points)
        if (profile.getCgpa() >= 8.0) score += 30;
        else if (profile.getCgpa() >= 7.0) score += 25;
        else if (profile.getCgpa() >= 6.0) score += 20;
        else score += 10;

        // Skills match (0-40 points)
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()
                && career.getRequiredSkills() != null) {

            String careerSkills = career.getRequiredSkills().toLowerCase();
            long matchedSkills = profile.getSkills().stream()
                    .filter(skill -> careerSkills.contains(skill.toLowerCase()))
                    .count();

            double ratio = (double) matchedSkills / Math.max(1, profile.getSkills().size());
            score += ratio * 40.0;
        }

        // Interests match (0-30 points)
        if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
            String careerDesc = career.getDescription() != null
                    ? career.getDescription().toLowerCase()
                    : "";

            long matchedInterests = profile.getInterests().stream()
                    .filter(interest -> careerDesc.contains(interest.toLowerCase()))
                    .count();

            double ratio = (double) matchedInterests / Math.max(1, profile.getInterests().size());
            score += ratio * 30.0;
        }

        // Career weight factor (avoid NPE and divide-by-zero)
        int weight = career.getRecommendationWeight() != null
                ? career.getRecommendationWeight()
                : 10;
        score *= (weight / 10.0);

        return Math.min(100.0, score);
    }

    private String generateMatchReason(StudentProfile profile, Career career, Double score) {
        StringBuilder reason = new StringBuilder();

        if (score >= 80) {
            reason.append("Excellent match. Your skills and interests align perfectly with this career. ");
        } else if (score >= 60) {
            reason.append("Good match. You have the foundational skills for this career. ");
        } else if (score >= 40) {
            reason.append("Fair match. Consider developing more relevant skills. ");
        } else {
            reason.append("Possible match. This career may require additional skill development. ");
        }

        reason.append("Your CGPA of ").append(profile.getCgpa())
                .append(" and background in ").append(profile.getAcademicBranch())
                .append(" are valuable for this role.");

        return reason.toString();
    }

    public List<RecommendationDTO> getUserRecommendations(Long userId) {
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(RecommendationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void updateRecommendationStatus(Long recommendationId, String status) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        recommendation.setStatus(Recommendation.RecommendationStatus.valueOf(status.toUpperCase()));
        recommendationRepository.save(recommendation);
    }
}
