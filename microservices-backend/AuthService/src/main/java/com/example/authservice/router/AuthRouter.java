package com.example.authservice.router;

import com.example.authservice.handler.AuthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class AuthRouter {

    @Bean
    public RouterFunction<ServerResponse> authRoutes(AuthHandler handler) {
        return RouterFunctions.route()
                .path("/api/auth", builder -> builder
                        .POST("/register", accept(MediaType.APPLICATION_JSON), handler::register)
                        .POST("/login", accept(MediaType.APPLICATION_JSON), handler::login)
                        .POST("/logout", handler::logout)
                        .GET("/validate", handler::validate)
                        .GET("/users", handler::getAllUsers)
                        .GET("/users/{id}", handler::getUserById)
                        .PUT("/users/{id}", accept(MediaType.APPLICATION_JSON), handler::updateUser)
                        .DELETE("/users/{id}", handler::deleteUser)
                )
                .build();
    }
}
