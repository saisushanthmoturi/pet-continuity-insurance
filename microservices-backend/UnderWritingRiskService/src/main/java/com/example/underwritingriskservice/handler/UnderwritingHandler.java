package com.example.underwritingriskservice.handler;

import com.example.underwritingriskservice.dto.QuoteRequest;
import com.example.underwritingriskservice.service.UnderwritingService;
import com.example.underwritingriskservice.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "UNDERWRITER", "ADMIN"}, () ->
                request.bodyToMono(QuoteRequest.class)
                        .flatMap(underwritingService::generateQuote)
                        .flatMap(quote -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(quote))
                        .onErrorResume(IllegalArgumentException.class, e ->
                                ServerResponse.status(HttpStatus.BAD_REQUEST)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(Map.of("error", e.getMessage())))
        );
    }

    public Mono<ServerResponse> getQuoteById(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "UNDERWRITER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return underwritingService.getQuoteById(id)
                    .flatMap(quote -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(quote))
                    .onErrorResume(IllegalArgumentException.class, e ->
                            ServerResponse.status(HttpStatus.NOT_FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getRiskMonitoring(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "UNDERWRITER", "ADMIN"}, () -> {
            Long petId = Long.valueOf(request.pathVariable("petId"));
            return underwritingService.getRiskMonitoring(petId)
                    .flatMap(monitoring -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(monitoring))
                    .onErrorResume(IllegalArgumentException.class, e ->
                            ServerResponse.status(HttpStatus.NOT_FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getAllQuotes(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"UNDERWRITER", "ADMIN"}, () ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(underwritingService.getAllQuotes(), Object.class)
        );
    }

    public Mono<ServerResponse> updateQuote(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"UNDERWRITER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(QuoteRequest.class)
                    .flatMap(req -> underwritingService.updateQuote(id, req))
                    .flatMap(quote -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(quote))
                    .onErrorResume(IllegalArgumentException.class, e ->
                            ServerResponse.status(HttpStatus.BAD_REQUEST)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> deleteQuote(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return underwritingService.deleteQuote(id)
                    .then(ServerResponse.noContent().build())
                    .onErrorResume(IllegalArgumentException.class, e ->
                            ServerResponse.status(HttpStatus.NOT_FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("error", e.getMessage())));
        });
    }
}
