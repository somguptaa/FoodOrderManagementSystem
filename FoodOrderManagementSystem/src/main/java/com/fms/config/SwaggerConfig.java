package com.fms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Food Order Management System API")
                        .version("1.0.0")
                        .description("REST API for managing food orders — create, track, and update orders through their full lifecycle.")
                        .contact(new Contact()
                                .name("Som Gupta")
                                .email("somgupta0011@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:2323")
                                .description("Local development server")));
    }
}