package com.college.career.AI_BasedCareerRecommendationSystem.Service;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.CareerDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Career;
import com.college.career.AI_BasedCareerRecommendationSystem.Repository.CareerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CareerService {

    @Autowired
    private CareerRepository careerRepository;

    public Career createCareer(CareerDTO careerDTO) {
        if (careerRepository.findByName(careerDTO.getName()).isPresent()) {
            throw new RuntimeException("Career already exists");
        }

        Career career = Career.builder()
                .name(careerDTO.getName())
                .description(careerDTO.getDescription())
                .requiredSkills(careerDTO.getRequiredSkills())
                .averageSalary(careerDTO.getAverageSalary())
                .jobOutlook(careerDTO.getJobOutlook())
                .recommendationWeight(careerDTO.getRecommendationWeight() != null ?
                        careerDTO.getRecommendationWeight() : 1)
                .isActive(true)
                .build();

        return careerRepository.save(career);
    }

    public List<CareerDTO> getAllActiveCareers() {
        return careerRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<Career> getCareerById(Long id) {
        return careerRepository.findById(id);
    }

    public Career updateCareer(Long id, CareerDTO careerDTO) {
        Career career = careerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Career not found"));

        career.setDescription(careerDTO.getDescription());
        career.setRequiredSkills(careerDTO.getRequiredSkills());
        career.setAverageSalary(careerDTO.getAverageSalary());
        career.setJobOutlook(careerDTO.getJobOutlook());
        career.setRecommendationWeight(careerDTO.getRecommendationWeight());

        return careerRepository.save(career);
    }

    public void deleteCareer(Long id) {
        Career career = careerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Career not found"));
        career.setIsActive(false);
        careerRepository.save(career);
    }

    private CareerDTO convertToDTO(Career career) {
        return new CareerDTO(
                career.getId(),
                career.getName(),
                career.getDescription(),
                career.getRequiredSkills(),
                career.getAverageSalary(),
                career.getJobOutlook(),
                career.getRecommendationWeight(),
                career.getIsActive()
        );
    }
}
