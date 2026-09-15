package com.example.authservice.handler;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AuthHandler {

    private final AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(RegisterRequest.class)
                .flatMap(authService::register)
                .flatMap(res -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(res))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(authService::login)
                .flatMap(res -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(res))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> validate(ServerRequest request) {
        String authHeader = request.headers().firstHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("error", "Missing or invalid Authorization header"));
        }
        String token = authHeader.substring(7);
        return authService.validateToken(token)
                .flatMap(claims -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(claims))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return com.example.authservice.util.SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(authService.getAllUsers(), Object.class)
        );
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        return com.example.authservice.util.SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return authService.getUserById(id)
                    .flatMap(u -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(u))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        return com.example.authservice.util.SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(com.example.authservice.dto.UserUpdateRequest.class)
                    .flatMap(req -> authService.updateUser(id, req))
                    .flatMap(u -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(u))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        return com.example.authservice.util.SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return authService.deleteUser(id)
                    .then(ServerResponse.noContent().build())
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }
}
