package com.app.backspark;

import io.swagger.v3.oas.models.OpenAPI;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class BackSparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackSparkApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI();
    }

}
