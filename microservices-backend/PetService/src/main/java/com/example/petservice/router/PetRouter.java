package com.example.petservice.router;

import com.example.petservice.handler.PetHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PetRouter {

    @Bean
    public RouterFunction<ServerResponse> petRoutes(PetHandler handler) {
        return RouterFunctions.route()
                .path("/api/pets", builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::createPet)
                        .GET("", handler::getAllPets)
                        .GET("/customer/{customerId}", handler::getPetsByCustomerId)
                        .POST("/{id}/medical-records", accept(MediaType.APPLICATION_JSON), handler::addMedicalRecord)
                        .GET("/{id}/medical-records", handler::getMedicalRecordsByPetId)
                        .DELETE("/medical-records/{recordId}", handler::deleteMedicalRecord)
                        .GET("/{id}", handler::getPetById)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON), handler::updatePet)
                        .DELETE("/{id}", handler::deletePet)
                )
                .build();
    }
}
