package com.example.careverificationservice.router;

import com.example.careverificationservice.handler.CareHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class CareRouter {

    @Bean
    public RouterFunction<ServerResponse> careRoutes(CareHandler handler) {
        return RouterFunctions.route()
                .path("/api/care", builder -> builder
                        .POST("/caretakers", accept(MediaType.APPLICATION_JSON), handler::addCaretaker)
                        .GET("/caretakers/pet/{petId}", handler::getCaretakersByPetId)
                        .POST("/caretakers/{id}/status", accept(MediaType.APPLICATION_JSON), handler::updateCaretakerStatus)
                        .POST("/care-plans", accept(MediaType.APPLICATION_JSON), handler::saveCarePlan)
                        .GET("/care-plans/pet/{petId}", handler::getCarePlanByPetId)
                        .POST("/verifications", accept(MediaType.APPLICATION_JSON), handler::recordVerification)
                        .GET("/verifications/pet/{petId}", handler::getVerificationsByPetId)
                        .GET("/eligibility/check", handler::checkEligibility)
                )
                .build();
    }
}
