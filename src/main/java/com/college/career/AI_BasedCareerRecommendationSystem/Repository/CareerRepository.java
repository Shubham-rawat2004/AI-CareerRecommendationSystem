package com.college.career.AI_BasedCareerRecommendationSystem.Repository;

import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {
    Optional<Career> findByName(String name);

    List<Career> findByIsActiveTrue();
}

