package com.example.claimsservice.handler;

import com.example.claimsservice.dto.ClaimRequest;
import com.example.claimsservice.dto.DeathVerificationRequest;
import com.example.claimsservice.service.ClaimsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ClaimsHandler {

    private final ClaimsService claimsService;

    public ClaimsHandler(ClaimsService claimsService) {
        this.claimsService = claimsService;
    }

    public Mono<ServerResponse> fileClaim(ServerRequest request) {
        return request.bodyToMono(ClaimRequest.class)
                .flatMap(claimsService::fileClaim)
                .flatMap(claim -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(claim))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> verifyDeath(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(DeathVerificationRequest.class)
                .defaultIfEmpty(new DeathVerificationRequest(true, "Auto-verified mock registry"))
                .flatMap(req -> claimsService.verifyDeath(id, req))
                .flatMap(claim -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(claim))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> investigate(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return claimsService.investigateClaim(id)
                .flatMap(claim -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(claim))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return claimsService.getById(id)
                .flatMap(claim -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(claim))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getByPolicyId(ServerRequest request) {
        Long policyId = Long.valueOf(request.pathVariable("policyId"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(claimsService.getByPolicyId(policyId), Object.class);
    }
}
