package com.example.policyservice.router;

import com.example.policyservice.handler.PolicyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PolicyRouter {

    @Bean
    public RouterFunction<ServerResponse> policyRoutes(PolicyHandler handler) {
        return RouterFunctions.route()
                .path("/api/policies", builder -> builder
                        .POST("/from-quote/{quoteId}", handler::createFromQuote)
                        .POST("/{id}/activate", handler::activate)
                        .POST("/{id}/status", accept(MediaType.APPLICATION_JSON), handler::updateStatus)
                        .GET("", handler::getAll)
                        .GET("/by-number/{policyNumber}", handler::getByNumber)
                        .GET("/customer/{customerId}", handler::getByCustomerId)
                        .GET("/{id}", handler::getById)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON), handler::updatePolicy)
                        .DELETE("/{id}", handler::deletePolicy)
                )
                .build();
    }
}
