package com.college.career.AI_BasedCareerRecommendationSystem.Repository;


import com.college.career.AI_BasedCareerRecommendationSystem.Entity.SkillQuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillQuizRepository extends JpaRepository<SkillQuizResponse, Long> {
    List<SkillQuizResponse> findByUserIdOrderByCompletedAtDesc(Long userId);
}
