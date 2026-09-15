package com.example.policyservice.handler;

import com.example.policyservice.dto.StatusUpdateRequest;
import com.example.policyservice.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class PolicyHandler {

    private final PolicyService policyService;

    public PolicyHandler(PolicyService policyService) {
        this.policyService = policyService;
    }

    public Mono<ServerResponse> createFromQuote(ServerRequest request) {
        Long quoteId = Long.valueOf(request.pathVariable("quoteId"));
        return policyService.createPolicyFromQuote(quoteId)
                .flatMap(policy -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(policy))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> activate(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return policyService.activatePolicy(id)
                .flatMap(policy -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(policy))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return policyService.getById(id)
                .flatMap(policy -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(policy))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getByNumber(ServerRequest request) {
        String policyNumber = request.pathVariable("policyNumber");
        return policyService.getByPolicyNumber(policyNumber)
                .flatMap(policy -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(policy))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getByCustomerId(ServerRequest request) {
        Long customerId = Long.valueOf(request.pathVariable("customerId"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(policyService.getByCustomerId(customerId), Object.class);
    }

    public Mono<ServerResponse> updateStatus(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(StatusUpdateRequest.class)
                .flatMap(req -> policyService.updateStatus(id, req.status(), req.reason()))
                .flatMap(policy -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(policy))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }
}
