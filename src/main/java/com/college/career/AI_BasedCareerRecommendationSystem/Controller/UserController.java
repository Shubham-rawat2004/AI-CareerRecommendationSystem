package com.college.career.AI_BasedCareerRecommendationSystem.Controller;

import com.college.career.AI_BasedCareerRecommendationSystem.DTO.ApiResponse;
import com.college.career.AI_BasedCareerRecommendationSystem.DTO.UserLoginDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.DTO.UserRegistrationDTO;
import com.college.career.AI_BasedCareerRecommendationSystem.Entity.User;
import com.college.career.AI_BasedCareerRecommendationSystem.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for user registration, login, and profile management")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new student or admin account")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            User user = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login with email and password")
    public ResponseEntity<ApiResponse<User>> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        var user = userService.loginUser(loginDTO);
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Login successful", user.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid email or password"));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        var user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user profile information")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody UserRegistrationDTO updateDTO) {
        try {
            User user = userService.updateUser(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

