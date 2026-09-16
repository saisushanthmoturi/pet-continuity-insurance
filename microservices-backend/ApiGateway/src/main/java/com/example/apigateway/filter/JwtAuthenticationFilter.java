package com.example.apigateway.filter;

import com.example.apigateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    // Public endpoints that do not require JWT
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/validate",
            "/fallback",
            "/actuator"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Allow CORS preflight requests
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Allow public endpoints
        for (String publicEndpoint : PUBLIC_ENDPOINTS) {
            if (path.startsWith(publicEndpoint)) {
                return chain.filter(exchange);
            }
        }

        // 1. Check HttpOnly Cookie "jwt_token" first
        String token = null;
        HttpCookie cookie = request.getCookies().getFirst("jwt_token");
        if (cookie != null && !cookie.getValue().isBlank()) {
            token = cookie.getValue().trim();
        } else {
            // 2. Fallback to Authorization: Bearer <token>
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7).trim();
            }
        }

        if (token == null) {
            log.warn("Unauthorized access attempt to {}: Missing token in Cookie or Authorization header", path);
            return onError(exchange, "Missing or invalid token in Cookie or Authorization header", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtUtil.isTokenValid(token)) {
            log.warn("Unauthorized access attempt to {}: Invalid or expired JWT token", path);
            return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
        }

        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            Object userId = claims.get("userId");
            Object role = claims.get("role");
            String email = claims.getSubject();

            // Mutate request to inject user details into downstream request headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId != null ? String.valueOf(userId) : "")
                    .header("X-User-Role", role != null ? String.valueOf(role) : "")
                    .header("X-User-Email", email != null ? email : "")
                    .build();

            log.debug("JWT validated for path={}, user={}, role={}", path, email, role);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("Failed to parse JWT claims for path {}: {}", path, e.getMessage());
            return onError(exchange, "Invalid token claims", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\": \"" + message + "\", \"status\": " + status.value() + "}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // Run early in the filter chain
    }
}
