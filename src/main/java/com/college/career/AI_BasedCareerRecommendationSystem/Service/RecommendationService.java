package com.college.career.AI_BasedCareerRecommendationSystem.Service;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.RecommendationDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Career;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Recommendation;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.SkillQuizResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.StudentProfile;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.User;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.CareerRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.RecommendationRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.StudentProfileRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private SkillQuizService skillQuizService;

    // ------------------------------------------------------------------
    // Generate recommendations for a user
    // ------------------------------------------------------------------
    public List<RecommendationDTO> generateRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        List<Career> allCareers = careerRepository.findByIsActiveTrue();
        List<Recommendation> recommendations = new ArrayList<>();

        // Latest skill quiz response (if any)
        List<SkillQuizResponse> skillResponses = skillQuizService.getUserSkillResponses(userId);
        Map<String, String> skillProficiencies =
                skillResponses.isEmpty() ? null : skillResponses.get(0).getSkillProficiencies();

        for (Career career : allCareers) {
            Double matchScore = calculateMatchScore(profile, career, skillProficiencies);
            String matchReason = generateMatchReason(profile, career, matchScore, skillProficiencies);

            Recommendation recommendation = Recommendation.builder()
                    .user(user)
                    .career(career)
                    .matchScore(matchScore)
                    .matchReason(matchReason)
                    .status(Recommendation.RecommendationStatus.PENDING)
                    .build();

            recommendations.add(recommendation);
        }

        // Sort by score desc and take top 5
        recommendations = recommendations.stream()
                .sorted((r1, r2) -> r2.getMatchScore().compareTo(r1.getMatchScore()))
                .limit(5)
                .collect(Collectors.toList());

        recommendationRepository.saveAll(recommendations);

        return recommendations.stream()
                .map(RecommendationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Latest ACCEPTED recommendation for history tab
    // ------------------------------------------------------------------
    public RecommendationDTO getLatestAcceptedRecommendation(Long userId) {
        return recommendationRepository
                .findLatestAccepted(
                        userId,
                        Recommendation.RecommendationStatus.ACCEPTED
                )
                .stream()
                .findFirst()
                .map(RecommendationDTO::fromEntity)
                .orElse(null);
    }



    // ------------------------------------------------------------------
    // Internal scoring logic
    // ------------------------------------------------------------------
    private Double calculateMatchScore(StudentProfile profile,
                                       Career career,
                                       Map<String, String> skillProficiencies) {
        double score = 0.0;

        // CGPA factor (0–25)
        if (profile.getCgpa() >= 8.0) score += 25;
        else if (profile.getCgpa() >= 7.0) score += 20;
        else if (profile.getCgpa() >= 6.0) score += 15;
        else score += 10;

        // ADVANCED SKILL BOOST (0–50)
        double advancedBoost = 0.0;
        if (skillProficiencies != null && !skillProficiencies.isEmpty()) {
            String careerSkills = career.getRequiredSkills() != null
                    ? career.getRequiredSkills().toLowerCase()
                    : "";

            Map<String, String[]> relatedSkills = Map.of(
                    "java", new String[]{"spring boot", "hibernate", "maven", "spring"},
                    "python", new String[]{"django", "flask", "pandas", "numpy"},
                    "html", new String[]{"css", "javascript", "react", "vue"},
                    "css", new String[]{"javascript", "react", "tailwind", "bootstrap"},
                    "sql", new String[]{"postgresql", "mysql", "mongodb", "oracle"},
                    "docker", new String[]{"aws", "kubernetes", "jenkins", "terraform"},
                    "aws", new String[]{"docker", "kubernetes", "terraform", "lambda"}
            );

            for (Map.Entry<String, String> entry : skillProficiencies.entrySet()) {
                String skill = entry.getKey().toLowerCase().trim();
                String level = entry.getValue().toLowerCase().trim();

                // Advanced skill directly in required skills
                if ("advanced".equals(level) && careerSkills.contains(skill)) {
                    advancedBoost += 50.0 / Math.max(1, skillProficiencies.size());

                    // Related skills bonus
                    if (relatedSkills.containsKey(skill)) {
                        for (String related : relatedSkills.get(skill)) {
                            if (careerSkills.contains(related.toLowerCase())) {
                                advancedBoost += 5.0;
                            }
                        }
                    }
                }
            }
        }
        score += Math.min(50.0, advancedBoost); // cap at 50

        // Regular skills match (0–15)
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()
                && career.getRequiredSkills() != null) {
            String careerSkills = career.getRequiredSkills().toLowerCase();
            long matchedSkills = profile.getSkills().stream()
                    .map(String::toLowerCase)
                    .filter(careerSkills::contains)
                    .count();
            double ratio = (double) matchedSkills / Math.max(1, profile.getSkills().size());
            score += ratio * 15.0;
        }

        // Interests match (0–10)
        if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
            String careerDesc = career.getDescription() != null
                    ? career.getDescription().toLowerCase()
                    : "";
            long matchedInterests = profile.getInterests().stream()
                    .map(String::toLowerCase)
                    .filter(careerDesc::contains)
                    .count();
            double ratio = (double) matchedInterests / Math.max(1, profile.getInterests().size());
            score += ratio * 10.0;
        }

        // Weight multiplier
        int weight = career.getRecommendationWeight() != null
                ? career.getRecommendationWeight()
                : 10;
        score *= (weight / 10.0);

        return Math.min(100.0, score);
    }

    // ------------------------------------------------------------------
    // Human-readable match reason
    // ------------------------------------------------------------------
    private String generateMatchReason(StudentProfile profile,
                                       Career career,
                                       Double score,
                                       Map<String, String> skillProficiencies) {
        StringBuilder reason = new StringBuilder();

        if (score >= 80) {
            reason.append("Excellent match. ");
        } else if (score >= 60) {
            reason.append("Good match. ");
        } else if (score >= 40) {
            reason.append("Fair match. ");
        } else {
            reason.append("Possible match. ");
        }

        if (skillProficiencies != null && career.getRequiredSkills() != null) {
            String careerSkills = career.getRequiredSkills().toLowerCase();

            List<String> advancedMatches = skillProficiencies.entrySet().stream()
                    .filter(entry -> "advanced".equalsIgnoreCase(entry.getValue())
                            && careerSkills.contains(entry.getKey().toLowerCase()))
                    .map(Map.Entry::getKey)
                    .toList();

            if (!advancedMatches.isEmpty()) {
                reason.append("Your advanced skills in ")
                        .append(String.join(", ", advancedMatches))
                        .append(" are highly relevant. ");
            }
        }

        reason.append("CGPA ").append(profile.getCgpa())
                .append(" and ")
                .append(profile.getAcademicBranch())
                .append(" background align well for this career.");

        return reason.toString();
    }

    // ------------------------------------------------------------------
    // All recommendations for user (sorted, used by history)
    // ------------------------------------------------------------------
    public List<RecommendationDTO> getUserRecommendations(Long userId) {
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(RecommendationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Update status: ACCEPTED / REJECTED
    // ------------------------------------------------------------------
    public void updateRecommendationStatus(Long recommendationId, String status) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        recommendation.setStatus(
                Recommendation.RecommendationStatus.valueOf(status.toUpperCase())
        );
        recommendationRepository.save(recommendation);
    }
}
