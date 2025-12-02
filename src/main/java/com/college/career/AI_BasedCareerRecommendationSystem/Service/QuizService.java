package com.college.career.AI_BasedCareerRecommendationSystem.Service;

import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Quiz;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.QuizQuestion;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.QuizResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.User;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.QuizRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.QuizResponseRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizResponseRepository quizResponseRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findByIsActiveTrue();
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public QuizResponse submitQuizResponse(Long userId, Long quizId, Map<Long, Integer> answers) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int score = calculateScore(quiz, answers);
        double percentage = (double) score / quiz.getQuestions().size() * 100;

        QuizResponse response = QuizResponse.builder()
                .user(user)
                .quiz(quiz)
                .score(score)
                .percentage(percentage)
                .answers(answers)
                .build();

        return quizResponseRepository.save(response);
    }

    private int calculateScore(Quiz quiz, Map<Long, Integer> answers) {
        int score = 0;

        for (QuizQuestion question : quiz.getQuestions()) {
            Integer selectedOption = answers.get(question.getId());
            if (selectedOption != null && selectedOption.equals(question.getCorrectOptionIndex())) {
                score++;
            }
        }

        return score;
    }

    public List<QuizResponse> getUserQuizResponses(Long userId) {
        return quizResponseRepository.findByUserIdOrderByCompletedAtDesc(userId);
    }
}
