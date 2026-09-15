package com.example.careverificationservice.handler;

import com.example.careverificationservice.dto.CarePlanRequest;
import com.example.careverificationservice.dto.CaretakerRequest;
import com.example.careverificationservice.dto.VerificationRequest;
import com.example.careverificationservice.service.CareService;
import com.example.careverificationservice.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class CareHandler {

    private final CareService careService;

    public CareHandler(CareService careService) {
        this.careService = careService;
    }

    public Mono<ServerResponse> addCaretaker(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN"}, () ->
                request.bodyToMono(CaretakerRequest.class)
                        .flatMap(careService::addCaretaker)
                        .flatMap(c -> ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).bodyValue(c))
                        .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())))
        );
    }

    public Mono<ServerResponse> getCaretakersByPetId(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "CARETAKER", "ADMIN", "UNDERWRITER"}, () -> {
            Long petId = Long.valueOf(request.pathVariable("petId"));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(careService.getCaretakersByPetId(petId), Object.class);
        });
    }

    public Mono<ServerResponse> updateCaretakerStatus(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(Map.class)
                    .flatMap(m -> {
                        String status = (String) m.get("status");
                        return careService.updateCaretakerStatus(id, status);
                    })
                    .flatMap(c -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(c))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> saveCarePlan(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN"}, () ->
                request.bodyToMono(CarePlanRequest.class)
                        .flatMap(careService::saveCarePlan)
                        .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p))
                        .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())))
        );
    }

    public Mono<ServerResponse> getCarePlanByPetId(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "CARETAKER", "ADMIN", "UNDERWRITER"}, () -> {
            Long petId = Long.valueOf(request.pathVariable("petId"));
            return careService.getCarePlanByPetId(petId)
                    .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> recordVerification(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "ADMIN"}, () ->
                request.bodyToMono(VerificationRequest.class)
                        .flatMap(careService::recordVerification)
                        .flatMap(v -> ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).bodyValue(v))
                        .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())))
        );
    }

    public Mono<ServerResponse> getVerificationsByPetId(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "ADMIN", "CUSTOMER"}, () -> {
            Long petId = Long.valueOf(request.pathVariable("petId"));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(careService.getVerificationsByPetId(petId), Object.class);
        });
    }

    public Mono<ServerResponse> getAllCaretakers(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(careService.getAllCaretakers(), Object.class)
        );
    }

    public Mono<ServerResponse> getCaretakerById(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "CARETAKER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return careService.getCaretakerById(id)
                    .flatMap(c -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(c))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> updateCaretaker(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "CARETAKER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(CaretakerRequest.class)
                    .flatMap(req -> careService.updateCaretaker(id, req))
                    .flatMap(c -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(c))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> deleteCaretaker(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return careService.deleteCaretaker(id)
                    .then(ServerResponse.noContent().build())
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> transferBackup(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long petId = Long.valueOf(request.pathVariable("petId"));
            return careService.transferToBackup(petId)
                    .flatMap(c -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(c))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getAllCarePlans(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(careService.getAllCarePlans(), Object.class)
        );
    }

    public Mono<ServerResponse> getCarePlanById(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "CARETAKER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return careService.getCarePlanById(id)
                    .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> deleteCarePlan(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return careService.deleteCarePlan(id)
                    .then(ServerResponse.noContent().build())
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getAllVerifications(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN", "CLAIMS_OFFICER"}, () ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(careService.getAllVerifications(), Object.class)
        );
    }

    public Mono<ServerResponse> getVerificationById(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return careService.getVerificationById(id)
                    .flatMap(v -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(v))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> deleteVerification(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return careService.deleteVerification(id)
                    .then(ServerResponse.noContent().build())
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> checkEligibility(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN", "CARETAKER"}, () -> {
            Long petId = Long.valueOf(request.queryParam("petId").orElse("1"));
            Long caretakerId = Long.valueOf(request.queryParam("caretakerId").orElse("1"));

            return careService.checkMonthlyEligibility(petId, caretakerId)
                    .flatMap(resp -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(resp))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }
}
