package com.college.career.AI_BasedCareerRecommendationSystem.Service;


import com.college.career.AI_BasedCareerRecommendationSystem.Entity.SkillQuizResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.User;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.SkillQuizRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SkillQuizService {
    @Autowired
    private SkillQuizRepository skillQuizRepository;
    @Autowired
    private UserRepository userRepository;

    public SkillQuizResponse submitSkillQuiz(Long userId, Map<String, String> skillProficiencies) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SkillQuizResponse response = SkillQuizResponse.builder()
                .user(user)
                .skillProficiencies(skillProficiencies)
                .build();

        return skillQuizRepository.save(response);
    }

    public List<SkillQuizResponse> getUserSkillResponses(Long userId) {
        return skillQuizRepository.findByUserIdOrderByCompletedAtDesc(userId);
    }
}
