FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/SocialMediaApp-0.0.1-SNAPSHOT.jar /app/SocialMediaApp-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "SocialMediaApp-0.0.1-SNAPSHOT.jar"]