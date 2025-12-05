package com.college.career.AI_BasedCareerRecommendationSystem.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI defineOpenApi() {

        Server server = new Server()
                .url("http://localhost:8080")   // FIXED: removed /api
                .description("Development Server");

        Contact myContact = new Contact()
                .name("Career System Team")
                .email("shub1808@gmail.com");

        Info information = new Info()
                .title("AI-Powered Career Recommendation System API")
                .version("1.0")
                .description("Complete REST API for AI-Powered Career Recommendation System")
                .contact(myContact)
                .license(new License().name("Apache 2.0"));

        return new OpenAPI()
                .info(information)
                .servers(List.of(server));
    }
}
