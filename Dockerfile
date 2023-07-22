FROM openjdk:17-jdk-alpine
COPY target/SocialMediaApp-0.0.1-SNAPSHOT.jar /SocialMediaApp-0.0.1-SNAPSHOT.jar




CMD ["java", "-jar", "SocialMediaApp-0.0.1-SNAPSHOT.jar"]