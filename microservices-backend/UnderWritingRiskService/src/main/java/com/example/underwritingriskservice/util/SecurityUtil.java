package com.example.underwritingriskservice.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static String getRole(ServerRequest request) {
        String role = request.headers().firstHeader("X-User-Role");
        return role != null ? role.toUpperCase() : "ANONYMOUS";
    }

    public static boolean hasAnyRole(ServerRequest request, String... allowedRoles) {
        String userRole = getRole(request);
        if ("ANONYMOUS".equals(userRole) || "INTERNAL_SERVICE".equals(userRole)) {
            return true; // Internal service-to-service call
        }
        for (String role : allowedRoles) {
            if (role.equalsIgnoreCase(userRole)) {
                return true;
            }
        }
        return false;
    }

    public static Mono<ServerResponse> checkRole(ServerRequest request, String[] allowedRoles, Supplier<Mono<ServerResponse>> action) {
        if (!hasAnyRole(request, allowedRoles)) {
            return ServerResponse.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "error", "Access denied: Required role: " + Arrays.toString(allowedRoles) + ", current role: " + getRole(request),
                            "status", 403
                    ));
        }
        return action.get();
    }
}
