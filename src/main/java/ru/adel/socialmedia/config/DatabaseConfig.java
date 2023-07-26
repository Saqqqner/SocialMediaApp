package ru.adel.socialmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ru.adel.socialmedia.repositories")
public class DatabaseConfig {

}
