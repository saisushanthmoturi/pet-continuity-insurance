package com.example.customerservice.handler;

import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class CustomerHandler {

    private final CustomerService customerService;

    public CustomerHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerRequest.class)
                .flatMap(customerService::createCustomer)
                .flatMap(created -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(created))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return customerService.getById(id)
                .flatMap(c -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(c))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getCustomerByUserId(ServerRequest request) {
        Long userId = Long.valueOf(request.pathVariable("userId"));
        return customerService.getByUserId(userId)
                .flatMap(c -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(c))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(CustomerRequest.class)
                .flatMap(req -> customerService.updateCustomer(id, req))
                .flatMap(updated -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(updated))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }
}
