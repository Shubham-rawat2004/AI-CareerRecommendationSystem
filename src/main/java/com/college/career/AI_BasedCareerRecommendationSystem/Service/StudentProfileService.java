package com.college.career.AI_BasedCareerRecommendationSystem.Service;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.StudentProfileDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.StudentProfile;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.User;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.StudentProfileRepository;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class StudentProfileService {

    @Autowired
    private StudentProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    public StudentProfile createOrUpdateProfile(Long userId, StudentProfileDTO profileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<StudentProfile> existingProfile = profileRepository.findByUserId(userId);

        StudentProfile profile = existingProfile.orElse(
                StudentProfile.builder().user(user).build()
        );

        profile.setPhoneNumber(profileDTO.getPhoneNumber());
        profile.setAcademicBranch(profileDTO.getAcademicBranch());
        profile.setCgpa(profileDTO.getCgpa());
        profile.setBio(profileDTO.getBio());
        profile.setInterests(profileDTO.getInterests());
        profile.setSkills(profileDTO.getSkills());

        return profileRepository.save(profile);
    }

    public Optional<StudentProfile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    public StudentProfile updateProfile(Long profileId, StudentProfileDTO profileDTO) {
        StudentProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setPhoneNumber(profileDTO.getPhoneNumber());
        profile.setAcademicBranch(profileDTO.getAcademicBranch());
        profile.setCgpa(profileDTO.getCgpa());
        profile.setBio(profileDTO.getBio());
        profile.setInterests(profileDTO.getInterests());
        profile.setSkills(profileDTO.getSkills());

        return profileRepository.save(profile);
    }
}
