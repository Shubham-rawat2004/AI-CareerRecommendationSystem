FROM openjdk:21-ea-12-jdk
EXPOSE 8082
COPY target/AI-BasedCareerRecommendationSystem-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
