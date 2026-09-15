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
                        // Caretakers
                        .POST("/caretakers", accept(MediaType.APPLICATION_JSON), handler::addCaretaker)
                        .GET("/caretakers", handler::getAllCaretakers)
                        .GET("/caretakers/{id}", handler::getCaretakerById)
                        .PUT("/caretakers/{id}", accept(MediaType.APPLICATION_JSON), handler::updateCaretaker)
                        .DELETE("/caretakers/{id}", handler::deleteCaretaker)
                        .GET("/caretakers/pet/{petId}", handler::getCaretakersByPetId)
                        .POST("/caretakers/{id}/status", accept(MediaType.APPLICATION_JSON), handler::updateCaretakerStatus)
                        .POST("/backup-transfer/{petId}", handler::transferBackup)

                        // Care Plans
                        .POST("/care-plans", accept(MediaType.APPLICATION_JSON), handler::saveCarePlan)
                        .GET("/care-plans", handler::getAllCarePlans)
                        .GET("/care-plans/{id}", handler::getCarePlanById)
                        .DELETE("/care-plans/{id}", handler::deleteCarePlan)
                        .GET("/care-plans/pet/{petId}", handler::getCarePlanByPetId)

                        // Verifications
                        .POST("/verifications", accept(MediaType.APPLICATION_JSON), handler::recordVerification)
                        .POST("/verify-pet", accept(MediaType.APPLICATION_JSON), handler::recordVerification)
                        .GET("/verifications", handler::getAllVerifications)
                        .GET("/verifications/{id}", handler::getVerificationById)
                        .DELETE("/verifications/{id}", handler::deleteVerification)
                        .GET("/verifications/pet/{petId}", handler::getVerificationsByPetId)

                        // Eligibility
                        .GET("/eligibility/check", handler::checkEligibility)
                )
                .build();
    }
}
