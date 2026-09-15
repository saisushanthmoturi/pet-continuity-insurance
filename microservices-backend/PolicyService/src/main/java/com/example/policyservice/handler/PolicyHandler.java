package com.example.policyservice.handler;

import com.example.policyservice.dto.StatusUpdateRequest;
import com.example.policyservice.service.PolicyService;
import com.example.policyservice.util.SecurityUtil;
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
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN"}, () -> {
            Long quoteId = Long.valueOf(request.pathVariable("quoteId"));
            return policyService.createPolicyFromQuote(quoteId)
                    .flatMap(policy -> ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(policy))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> activate(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return policyService.activatePolicy(id)
                    .flatMap(policy -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(policy))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN", "CLAIMS_OFFICER", "CARETAKER"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return policyService.getById(id)
                    .flatMap(policy -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(policy))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getByNumber(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN", "CLAIMS_OFFICER"}, () -> {
            String policyNumber = request.pathVariable("policyNumber");
            return policyService.getByPolicyNumber(policyNumber)
                    .flatMap(policy -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(policy))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getByCustomerId(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN"}, () -> {
            Long customerId = Long.valueOf(request.pathVariable("customerId"));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(policyService.getByCustomerId(customerId), Object.class);
        });
    }

    public Mono<ServerResponse> updateStatus(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CLAIMS_OFFICER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(StatusUpdateRequest.class)
                    .flatMap(req -> policyService.updateStatus(id, req.status(), req.reason()))
                    .flatMap(policy -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(policy))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(policyService.getAll(), Object.class)
        );
    }

    public Mono<ServerResponse> updatePolicy(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(com.example.policyservice.model.Policy.class)
                    .flatMap(p -> policyService.updatePolicy(id, p))
                    .flatMap(policy -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(policy))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> deletePolicy(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return policyService.deletePolicy(id)
                    .then(ServerResponse.noContent().build())
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }
}
