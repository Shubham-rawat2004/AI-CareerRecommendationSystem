package com.college.career.AI_BasedCareerRecommendationSystem.Repository;

import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserId(Long userId);

    List<Recommendation> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Recommendation> findByCareerId(Long careerId);

    // SAFE + FIXED: JPQL works regardless of column naming
    @Query("SELECT r FROM Recommendation r " +
            "WHERE r.user.id = :userId AND r.status = :status " +
            "ORDER BY r.createdAt DESC")
    List<Recommendation> findLatestAccepted(
            @Param("userId") Long userId,
            @Param("status") Recommendation.RecommendationStatus status
    );
}
