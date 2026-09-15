package com.example.petservice.handler;

import com.example.petservice.dto.MedicalRecordRequest;
import com.example.petservice.dto.PetRequest;
import com.example.petservice.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class PetHandler {

    private final PetService petService;

    public PetHandler(PetService petService) {
        this.petService = petService;
    }

    public Mono<ServerResponse> createPet(ServerRequest request) {
        return request.bodyToMono(PetRequest.class)
                .flatMap(petService::createPet)
                .flatMap(pet -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pet))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getPetById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return petService.getPetById(id)
                .flatMap(pet -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(pet))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getPetsByCustomerId(ServerRequest request) {
        Long customerId = Long.valueOf(request.pathVariable("customerId"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(petService.getPetsByCustomerId(customerId), Object.class);
    }

    public Mono<ServerResponse> addMedicalRecord(ServerRequest request) {
        Long petId = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(MedicalRecordRequest.class)
                .flatMap(req -> petService.addMedicalRecord(petId, req))
                .flatMap(record -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(record))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getMedicalRecordsByPetId(ServerRequest request) {
        Long petId = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(petService.getMedicalRecordsByPetId(petId), Object.class);
    }
}
