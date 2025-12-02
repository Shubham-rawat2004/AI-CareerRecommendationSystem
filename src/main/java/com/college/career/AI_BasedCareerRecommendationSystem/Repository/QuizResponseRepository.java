package com.college.career.AI_BasedCareerRecommendationSystem.Repository;

import com.college.career.AI_BasedCareerRecommendationSystem.Entity.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResponseRepository extends JpaRepository<QuizResponse, Long> {
    List<QuizResponse> findByUserId(Long userId);

    List<QuizResponse> findByUserIdOrderByCompletedAtDesc(Long userId);
}