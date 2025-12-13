package com.college.career.AI_BasedCareerRecommendationSystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AIBasedCareerRecommendationSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIBasedCareerRecommendationSystemApplication.class, args);
        System.out.println("AI-Based Career Recommendation System");
        System.out.println(System.getProperty("java.version"));


    }
}

// What features do we have till now:-

//1. REGISTER → shub22000@gmail.com → Student profile
//2. TAKE QUIZ → Answer 4 career questions → Get score
//3. GENERATE RECOMMENDATIONS → AI matches: Software Developer (Java/Spring Boot)"
//        4. VIEW RECOMMENDATIONS** → GET /recommendations/user/1 → Top 5 careers ranked
//5. ACCEPT TOP CAREER** → PUT /recommendations/1/status?status=ACCEPTED
//6. RE-RUN AI → New quiz → Updated recommendations


//What Your Project Does (Interview Answer)
//Our AI-Based Career Recommendation System is a full-stack Spring Boot application that helps students
// discover perfect career matches by analyzing their skills, interests, and quiz responses.
// Users register, create detailed profiles with technical skills like Java/Spring Boot/SQL,
// take interactive career quizzes, and receive personalized recommendations ranked by match
// percentage—such as "96% Software Developer" for Java enthusiasts. The system features 22+ REST APIs
// including user management, career CRUD, recommendation generation (POST /recommendations/generate/1),
// status updates (accept/reject), and a complete quiz subsystem with dynamic MCQs, auto-scoring,
// and submission history, all backed by MySQL and documented via Swagger UI for seamless testing
// and deployment.

//START → REGISTER → PROFILE → QUIZ → RECOMMENDATIONS → CAREER PATH
//     ↓       ↓        ↓      ↓         ↓                 ↓
//LANDING  LOGIN/SIGNUP PROFILE  CAREER  PERSONALIZED  ACTION
//PAGE     (shub22000) COMPLETION QUIZ     MATCHES     (Apply/Learn)
