package com.example.underwritingriskservice.handler;

import com.example.underwritingriskservice.dto.QuoteRequest;
import com.example.underwritingriskservice.model.RatingRule;
import com.example.underwritingriskservice.service.UnderwritingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UnderwritingHandler {

    private final UnderwritingService underwritingService;

    public UnderwritingHandler(UnderwritingService underwritingService) {
        this.underwritingService = underwritingService;
    }

    public Mono<ServerResponse> generateQuote(ServerRequest request) {
        return request.bodyToMono(QuoteRequest.class)
                .flatMap(underwritingService::generateQuote)
                .flatMap(quote -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(quote))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getQuoteById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return underwritingService.getQuoteById(id)
                .flatMap(quote -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(quote))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getRiskMonitoring(ServerRequest request) {
        Long petId = Long.valueOf(request.pathVariable("petId"));
        return underwritingService.getRiskMonitoring(petId)
                .flatMap(monitoring -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(monitoring))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getAllQuotes(ServerRequest request) {
        return underwritingService.getAllQuotes()
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> getQuotesByCustomerId(ServerRequest request) {
        Long customerId = Long.valueOf(request.pathVariable("customerId"));
        return underwritingService.getQuotesByCustomerId(customerId)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> getQuotesByPetId(ServerRequest request) {
        Long petId = Long.valueOf(request.pathVariable("petId"));
        return underwritingService.getQuotesByPetId(petId)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> reassessPetRisk(ServerRequest request) {
        Long petId = Long.valueOf(request.pathVariable("petId"));
        return underwritingService.reassessPetRisk(petId)
                .flatMap(ass -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(ass))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> updateQuote(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(QuoteRequest.class)
                .flatMap(req -> underwritingService.updateQuote(id, req))
                .flatMap(quote -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(quote))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> deleteQuote(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return underwritingService.deleteQuote(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getAllRules(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(underwritingService.getAllRules(), Object.class)
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> getRuleById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return underwritingService.getRuleById(id)
                .flatMap(r -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(r))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> createRule(ServerRequest request) {
        return request.bodyToMono(RatingRule.class)
                .flatMap(underwritingService::createRule)
                .flatMap(r -> ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).bodyValue(r))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> updateRule(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(RatingRule.class)
                .flatMap(r -> underwritingService.updateRule(id, r))
                .flatMap(r -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(r))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> deleteRule(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return underwritingService.deleteRule(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getAssessmentByQuoteId(ServerRequest request) {
        Long quoteId = Long.valueOf(request.pathVariable("quoteId"));
        return underwritingService.getRiskAssessmentByQuoteId(quoteId)
                .flatMap(a -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(a))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }
}
