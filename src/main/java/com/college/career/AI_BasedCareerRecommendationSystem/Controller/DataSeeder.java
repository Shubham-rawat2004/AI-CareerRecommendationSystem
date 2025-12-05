package com.college.career.AI_BasedCareerRecommendationSystem.Controller;

import com.college.career.AI_BasedCareerRecommendationSystem.Entity.*;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class DataSeeder {

    @Autowired
    private QuizRepository quizRepo;

    @PostMapping("/seed-quizzes")
    @Transactional
    public String seed() {
        // Check if already seeded
        if (!quizRepo.findAll().isEmpty()) {
            return "✅ Quizzes already exist! Test GET /quizzes";
        }

        try {
            // Create Quiz
            Quiz quiz = Quiz.builder()
                    .title("Career Interest Quiz")
                    .description("Assess your career preferences")
                    .isActive(true)
                    .build();

            // Save quiz first
            Quiz savedQuiz = quizRepo.save(quiz);

            // Create Questions with Options
            List<QuizQuestion> questions = Arrays.asList(
                    QuizQuestion.builder()
                            .quiz(savedQuiz)
                            .questionText("What interests you most?")
                            .options(Arrays.asList("Programming", "Data Analysis", "Design", "Management"))
                            .correctOptionIndex(0)
                            .build(),

                    QuizQuestion.builder()
                            .quiz(savedQuiz)
                            .questionText("Preferred work environment?")
                            .options(Arrays.asList("Team", "Solo", "Remote", "Office"))
                            .correctOptionIndex(1)
                            .build(),

                    QuizQuestion.builder()
                            .quiz(savedQuiz)
                            .questionText("Technical skills level?")
                            .options(Arrays.asList("Beginner", "Intermediate", "Advanced", "Expert"))
                            .correctOptionIndex(2)
                            .build(),

                    QuizQuestion.builder()
                            .quiz(savedQuiz)
                            .questionText("Career timeline?")
                            .options(Arrays.asList("Fast track", "Steady growth", "Explore first", "Long term"))
                            .correctOptionIndex(0)
                            .build()
            );

            // Clear existing questions and add new ones
            savedQuiz.getQuestions().clear();
            savedQuiz.getQuestions().addAll(questions);

            // Save everything in transaction
            quizRepo.save(savedQuiz);

            return "🎉 SUCCESS! Seeded 1 Quiz + 4 Questions!\n" +
                    "✅ Test: GET /quizzes\n" +
                    "✅ Test: GET /quizzes/1\n" +
                    "✅ Test: POST /quizzes/submit/1/1";

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    @PostMapping("/seed-questions-only")
    @Transactional
    public String seedQuestionsOnly() {
        try {
            Quiz quiz = quizRepo.findById(1L).orElse(null);
            if (quiz == null) {
                return "❌ Quiz ID=1 not found! Run /seed-quizzes first";
            }

            // Create Questions for existing quiz
            List<QuizQuestion> questions = Arrays.asList(
                    QuizQuestion.builder()
                            .quiz(quiz)
                            .questionText("What interests you most?")
                            .options(Arrays.asList("Programming", "Data Analysis", "Design", "Management"))
                            .correctOptionIndex(0)
                            .build(),

                    QuizQuestion.builder()
                            .quiz(quiz)
                            .questionText("Preferred work environment?")
                            .options(Arrays.asList("Team", "Solo", "Remote", "Office"))
                            .correctOptionIndex(1)
                            .build(),

                    QuizQuestion.builder()
                            .quiz(quiz)
                            .questionText("Technical skills level?")
                            .options(Arrays.asList("Beginner", "Intermediate", "Advanced", "Expert"))
                            .correctOptionIndex(2)
                            .build(),

                    QuizQuestion.builder()
                            .quiz(quiz)
                            .questionText("Career timeline?")
                            .options(Arrays.asList("Fast track", "Steady growth", "Explore first", "Long term"))
                            .correctOptionIndex(0)
                            .build()
            );

            // Clear and add questions
            quiz.getQuestions().clear();
            quiz.getQuestions().addAll(questions);
            quizRepo.save(quiz);

            return "🎉 Added 4 Questions to Quiz #1!\n✅ Test: GET /quizzes/1";

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}
