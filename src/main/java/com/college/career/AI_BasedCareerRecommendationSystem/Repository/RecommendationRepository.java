package com.college.career.AI_BasedCareerRecommendationSystem.Repository;

import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserId(Long userId);

    List<Recommendation> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Recommendation> findByCareerId(Long careerId);
}
