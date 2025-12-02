package com.college.career.AI_BasedCareerRecommendationSystem.Repository;

import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByIsActiveTrue();
}