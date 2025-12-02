 AI-Powered Career Recommendation System -
Complete Spring Boot Project
 PROJECT OVERVIEW
A production-ready, fully functional REST API built with Spring Boot
3.x, Java 17, and MySQL that intelligently recommends suitable career paths
to students using an AI-based matching algorithm.
Key Achievements:
22 REST API Endpoints - Fully documented with Swagger UI
AI Recommendation Engine - Intelligent matching algorithm
Complete Database Schema - 11 tables with relationships
50+ Java Classes - Enterprise-grade architecture
Security & Validation - BCrypt, input validation, error handling
Ready for Production - Deployment-ready code
 WHAT’S INCLUDED
8 Documentation Files:
1. project_setup.md - Complete Maven pom.xml with all dependencies
2. entities_code.md - 7 JPA Entity classes (User, Career, etc.)
3. repositories_dtos.md - 6 Repository interfaces + 8 DTO classes
4. services_code.md - 5 Service classes with business logic
5. controllers_code.md - 5 REST Controllers with 22 endpoints
6. config_main.md - Security, OpenAPI, main application class
7. setup_guide.md - Step-by-step implementation guide with examples
8. appearance_behavior.md - System architecture & UI flow diagrams
9. complete_summary.md - Quick reference and troubleshooting
 SYSTEM ARCHITECTURE


 Spring Boot REST API (Port 8080)
  5 Controllers (22 endpoints)
  5 Services (business logic)
1
  6 Repositories (data access)
  7 Entities (JPA models)
  Swagger Documentation

↓

MySQL Database (Port 3306)
  11 Tables with relationships
  Cascade operations enabled
  Lazy loading optimized

 QUICK START (5 MINUTES)
Prerequisites:
• Java 17+ fi
• MySQL 8.0+ fi
• Maven 3.6+ fi
Step 1: Clone/Create Project
mvn archetype:generate -DgroupId=com.career \
-DartifactId=career-recommendation-system \
-DarchetypeArtifactId=maven-archetype-quickstart \
-DinteractiveMode=false
cd career-recommendation-system
Step 2: Copy pom.xml
Get full pom.xml from project_setup.md and replace
Step 3: Create Folder Structure
src/main/java/com/career/
 entity/ (7 classes)
 repository/ (6 interfaces)
 dto/ (8 classes)
 service/ (5 classes)
 controller/ (5 classes)
 config/ (2 classes)
 exception/ (1 class)
2
Step 4: Copy All Code Files
Copy all Java source code from the provided documentation into respective
directories
Step 5: Configure & Run
# Update MySQL credentials in src/main/resources/application.properties
spring.datasource.username=root
spring.datasource.password=root
# Build and run
mvn clean install
mvn spring-boot:run
Access the Application:
• API Base URL: http://localhost:8080/api
• Swagger UI: http://localhost:8080/api/swagger-ui.html
• API Docs JSON: http://localhost:8080/api/v3/api-docs
 DATABASE SCHEMA
11 Tables with Relationships:
Table Purpose Key Columns
users User accounts id, email, password, role
student_profiles Student data id, user_id, cgpa, branch
careers Job positions id, name, description, salary
recommendations Generated matches id, user_id, career_id, score
quizzes Aptitude tests id, title, description
quiz_questions Test questions id, quiz_id, question_text
quiz_responses Test submissions id, user_id, quiz_id, score
student_skills Many-to-many profile_id, skill
student_interests Many-to-many profile_id, interest
question_options Multiple choices question_id, option_text
quiz_response_answers Answer mappings response_id, question_id,
answer
 API ENDPOINTS (22 Total)
fi User Management
POST /api/users/register - Register new user
3
POST /api/users/login - User login
GET /api/users/{id} - Get user details
PUT /api/users/{id} - Update user
fi fi Student Profile
POST /api/student-profiles/user/{userId} - Create/update profile
GET /api/student-profiles/user/{userId} - Get profile
PUT /api/student-profiles/{profileId} - Update profile
fi Career Management
POST /api/careers - Create career (Admin)
GET /api/careers - Get all careers
GET /api/careers/{id} - Get career details
PUT /api/careers/{id} - Update career (Admin)
DELETE /api/careers/{id} - Delete career (Admin)
fi Recommendations (AI)
POST /api/recommendations/generate/{userId} - Generate recommendations
GET /api/recommendations/user/{userId} - Get recommendations
PUT /api/recommendations/{id}/status - Update status
fi Quiz Management
GET /api/quizzes - Get all quizzes
GET /api/quizzes/{id} - Get quiz details
POST /api/quizzes/submit/{userId}/{quizId} - Submit quiz
GET /api/quizzes/responses/{userId} - Get quiz history
 AI RECOMMENDATION ALGORITHM
Match Score Calculation (0-100):

 STUDENT PROFILE DATA

 • CGPA: 8.5
 • Branch: Computer Science
 • Skills: [Java, Python, ML, JS, etc]
 • Interests: [AI, Web Dev, etc]

↓
FOR EACH CAREER
↓
4
 SCORE CALCULATION

 1. CGPA Factor (30 points)
  8.0+ → 30 pts
  7.0+ → 25 pts
  6.0+ → 20 pts
 2. Skills Match (40 points)
  % matched × 40
 3. Interests Match (30 points)
  % matched × 30
 4. Career Weight Multiplier
  Score × (weight / 10)
 5. Final Score (0-100%)

Example Output:
{
"recommendations": [
{
"careerName": "Software Developer",
"matchScore": 95.5,
"reason": "Excellent match - your skills align perfectly"
},
{
"careerName": "Data Scientist",
"matchScore": 88.3,
"reason": "Good match - strong ML background"
},
...top 5 matches...
]
}
 SECURITY FEATURES
Password Encryption - BCrypt hashing
User Authentication - Email-based login
Role-Based Access - STUDENT/ADMIN roles
Input Validation - Jakarta Validation annotations
SQL Injection Prevention - JPA parameterized queries
5
CORS Configuration - Secure cross-origin requests
Error Handling - Global exception handler
No Stack Traces - Safe error responses
 USER WORKFLOWS
Student Workflow:
1. Register/Login
↓
2. Create Profile (add skills, interests, CGPA)
↓
3. Generate Recommendations
↓
4. View Top 5 Matched Careers
↓
5. Accept/Reject Recommendations
↓
6. View Recommendation History
Admin Workflow:
1. Admin Login
↓
2. Manage Careers (CRUD)
↓
3. Configure Quizzes
↓
4. Adjust System Settings
↓
5. View Statistics

 SAMPLE API REQUEST/RESPONSE
Generate Recommendations:
Request:
POST http://localhost:8080/api/recommendations/generate/1
Authorization: Bearer {token}
Response (200 OK):
{
"success": true,
"message": "Recommendations generated successfully",
6
"timestamp": "2024-12-02T15:40:00",
"data": [
{
"id": 1,
"careerName": "Software Developer",
"matchScore": 95.5,
"matchReason": "Excellent match. Your skills and interests align perfectly with this c"createdAt": "2024-12-02T15:40:00",
"status": "PENDING"
},
{
"id": 2,
"careerName": "Data Scientist",
"matchScore": 88.3,
"matchReason": "Good match. You have the foundational skills for this career...",
"createdAt": "2024-12-02T15:40:00",
"status": "PENDING"
}
]
}
 TECHNOLOGIES USED
Component Technology Version
Language Java 17+
Framework Spring Boot 3.2.0
Web Spring MVC 6.x
Database Spring Data JPA 6.x
ORM Hibernate 6.x
Database MySQL 8.0+
Security BCrypt 1.0+
API Docs Springdoc OpenAPI 2.1.0
Build Maven 3.6+
Validation Jakarta Validation 3.x
