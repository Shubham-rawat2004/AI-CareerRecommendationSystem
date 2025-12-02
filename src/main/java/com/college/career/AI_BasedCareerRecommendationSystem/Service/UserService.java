package com.college.career.AI_BasedCareerRecommendationSystem.Service;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.UserLoginDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.DTO.UserRegistrationDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.User;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(registrationDTO.getEmail())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .role(User.UserRole.valueOf(registrationDTO.getRole() != null ?
                        registrationDTO.getRole().toUpperCase() : "STUDENT"))
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> loginUser(UserLoginDTO loginDTO) {
        Optional<User> user = userRepository.findByEmail(loginDTO.getEmail());
        if (user.isPresent() &&
                passwordEncoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(Long id, UserRegistrationDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());

        return userRepository.save(user);
    }
}
