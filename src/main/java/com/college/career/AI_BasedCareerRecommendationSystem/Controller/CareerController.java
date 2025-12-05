package com.college.career.AI_BasedCareerRecommendationSystem.Controller;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.ApiResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.DTO.CareerDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.Career;
import com.college.career.AI_BasedCareerRecommendationSystem.Service.CareerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/careers")
@Tag(name = "Career Management", description = "APIs for managing career options")
public class CareerController {

    @Autowired
    private CareerService careerService;

    @PostMapping
    @Operation(summary = "Create career", description = "Add a new career option to the system")
    public ResponseEntity<ApiResponse<Career>> createCareer(@Valid @RequestBody CareerDTO careerDTO) {
        try {
            Career career = careerService.createCareer(careerDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Career created successfully", career));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all careers", description = "Retrieve all active career options")
    public ResponseEntity<ApiResponse<List<CareerDTO>>> getAllCareers() {
        List<CareerDTO> careers = careerService.getAllActiveCareers();
        return ResponseEntity.ok(ApiResponse.success("Careers retrieved successfully", careers));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get career by ID", description = "Retrieve career details by career ID")
    public ResponseEntity<ApiResponse<Career>> getCareerById(@PathVariable Long id) {
        var career = careerService.getCareerById(id);
        if (career.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Career retrieved successfully", career.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Career not found"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update career", description = "Update career information")
    public ResponseEntity<ApiResponse<Career>> updateCareer(
            @PathVariable Long id,
            @Valid @RequestBody CareerDTO careerDTO) {
        try {
            Career career = careerService.updateCareer(id, careerDTO);
            return ResponseEntity.ok(ApiResponse.success("Career updated successfully", career));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete career", description = "Deactivate a career option")
    public ResponseEntity<ApiResponse<Void>> deleteCareer(@PathVariable Long id) {
        try {
            careerService.deleteCareer(id);
            return ResponseEntity.ok(ApiResponse.success("Career deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
