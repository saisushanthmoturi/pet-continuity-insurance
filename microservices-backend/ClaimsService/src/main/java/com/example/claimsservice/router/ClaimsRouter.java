package com.example.claimsservice.router;

import com.example.claimsservice.handler.ClaimsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ClaimsRouter {

    @Bean
    public RouterFunction<ServerResponse> claimsRoutes(ClaimsHandler handler) {
        return RouterFunctions.route()
                .path("/api/claims", builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::fileClaim)
                        .POST("/{id}/verify-death", handler::verifyDeath)
                        .POST("/{id}/investigate", handler::investigate)
                        .GET("/policy/{policyId}", handler::getByPolicyId)
                        .GET("/{id}", handler::getById)
                )
                .build();
    }
}
