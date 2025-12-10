package com.college.career.AI_BasedCareerRecommendationSystem.Controller;

import com.college.career.AI_BasedCareerRecommendationSystem.Entity.*;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.CareerRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class DataSeeder {

    @Autowired
    private QuizRepository quizRepo;

    @Autowired
    private CareerRepository careerRepo;

    @PostMapping("/seed-quizzes")
    @Transactional
    public String seedQuizzes() {
        if (!quizRepo.findAll().isEmpty()) {
            return "❌ Quizzes exist! DELETE FROM quizzes; first";
        }

        try {
            Quiz quiz = Quiz.builder()
                    .title("Career Interest Quiz")
                    .description("Assess your career preferences")
                    .isActive(true)
                    .build();

            Quiz savedQuiz = quizRepo.save(quiz);

            // ✅ SIMPLE: Save questions directly (cascade works)
            QuizQuestion q1 = QuizQuestion.builder()
                    .quiz(savedQuiz).questionText("What interests you most?")
                    .options(Arrays.asList("Programming", "Data Analysis", "Design", "Management"))
                    .correctOptionIndex(0).build();

            QuizQuestion q2 = QuizQuestion.builder()
                    .quiz(savedQuiz).questionText("Preferred work environment?")
                    .options(Arrays.asList("Team", "Solo", "Remote", "Office"))
                    .correctOptionIndex(1).build();

            QuizQuestion q3 = QuizQuestion.builder()
                    .quiz(savedQuiz).questionText("Technical skills level?")
                    .options(Arrays.asList("Beginner", "Intermediate", "Advanced", "Expert"))
                    .correctOptionIndex(2).build();

            QuizQuestion q4 = QuizQuestion.builder()
                    .quiz(savedQuiz).questionText("Career timeline?")
                    .options(Arrays.asList("Fast track", "Steady growth", "Explore first", "Long term"))
                    .correctOptionIndex(0).build();

            // Cascade saves everything
            savedQuiz.getQuestions().add(q1);
            savedQuiz.getQuestions().add(q2);
            savedQuiz.getQuestions().add(q3);
            savedQuiz.getQuestions().add(q4);

            quizRepo.save(savedQuiz);

            return "✅ SUCCESS! 1 Quiz + 4 Questions! GET /quizzes/1";
        } catch (Exception e) {
            return "❌ " + e.getMessage();
        }
    }

    @PostMapping("/seed-careers")
    @Transactional
    public String seedCareers() {
        if (!careerRepo.findAll().isEmpty()) {
            return "Careers exist! DELETE FROM careers; first";
        }

        careerRepo.saveAll(Arrays.asList(
                Career.builder().name("Software Developer").requiredSkills("Java,Python").recommendationWeight(10).isActive(true).build(),
                Career.builder().name("Data Analyst").requiredSkills("SQL,Excel").recommendationWeight(9).isActive(true).build(),
                Career.builder().name("Web Designer").requiredSkills("HTML,CSS").recommendationWeight(8).isActive(true).build(),
                Career.builder().name("Graphic Designer").requiredSkills("Photoshop").recommendationWeight(7).isActive(true).build(),
                Career.builder().name("DevOps Engineer").requiredSkills("Docker,AWS").recommendationWeight(10).isActive(true).build()
        ));

        return "✅ SUCCESS! 5 Careers! GET /careers";
    }
}
