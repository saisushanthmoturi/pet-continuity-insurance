package com.example.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter jwtAuthFilter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        jwtAuthFilter.setServerAuthenticationConverter(serverAuthenticationConverter());

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            byte[] bytes = "{\"error\": \"Unauthorized: Missing or invalid JWT token\", \"status\": 401}".getBytes(StandardCharsets.UTF_8);
                            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                        })
                        .accessDeniedHandler((exchange, ex) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            byte[] bytes = "{\"error\": \"Forbidden: You do not have permission to perform this action\", \"status\": 403}".getBytes(StandardCharsets.UTF_8);
                            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                        })
                )
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints: Registration, Login, Token Validation, Actuator, Fallback, Pre-flight OPTIONS
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/api/auth/register", "/api/auth/login", "/api/auth/validate", "/actuator/**", "/fallback/**").permitAll()

                        // ADMIN ONLY: User management
                        .pathMatchers("/api/auth/users/**").hasAuthority("ROLE_ADMIN")

                        // Resource deletion across all services - restricted to ADMIN
                        .pathMatchers(HttpMethod.DELETE, "/api/customers/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/pets/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/policies/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/underwriting/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/claims/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/payments/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/funds/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/care/**").hasAuthority("ROLE_ADMIN")

                        // Customer Service Endpoints
                        .pathMatchers(HttpMethod.POST, "/api/customers").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/customers").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/customers/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_UNDERWRITER", "ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/customers/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")

                        // Pet Service Endpoints
                        .pathMatchers(HttpMethod.POST, "/api/pets", "/api/pets/*/medical-records").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/pets").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/pets/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_UNDERWRITER", "ROLE_CLAIMS_OFFICER", "ROLE_CARETAKER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/pets/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")

                        // Underwriting & Risk Service Endpoints
                        .pathMatchers(HttpMethod.POST, "/api/underwriting/quotes").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/underwriting/quotes/*/accept").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/underwriting/quotes").hasAnyAuthority("ROLE_UNDERWRITER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/underwriting/quotes/*").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_UNDERWRITER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/underwriting/quotes/**").hasAnyAuthority("ROLE_UNDERWRITER", "ROLE_ADMIN")
                        .pathMatchers("/api/underwriting/**").hasAnyAuthority("ROLE_UNDERWRITER", "ROLE_ADMIN")

                        // Policy Service Endpoints
                        .pathMatchers(HttpMethod.POST, "/api/policies").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/policies/*/activate").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/policies").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/policies/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_UNDERWRITER", "ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/policies/**").hasAnyAuthority("ROLE_ADMIN")

                        // Claims Service Endpoints
                        .pathMatchers(HttpMethod.POST, "/api/claims").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/claims/*/verify-death").hasAnyAuthority("ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/claims/*/investigate").hasAnyAuthority("ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/claims/*/approve").hasAnyAuthority("ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/claims/*/reject").hasAnyAuthority("ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/claims").hasAnyAuthority("ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/claims/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/claims/**").hasAnyAuthority("ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")

                        // Payment & Fund Service Endpoints
                        .pathMatchers(HttpMethod.POST, "/api/payments/premium").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/funds/*/disburse", "/api/payments/funds/*/disburse").hasAnyAuthority("ROLE_CARETAKER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/funds/*/expense", "/api/payments/funds/*/expense").hasAnyAuthority("ROLE_CARETAKER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/funds").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/funds/**", "/api/payments/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CARETAKER", "ROLE_ADMIN")

                        // Care Verification Service Endpoints
                        .pathMatchers(HttpMethod.POST, "/api/care/caretakers", "/api/care/care-plans").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/care/verifications", "/api/care/verify-pet").hasAnyAuthority("ROLE_CARETAKER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/care/backup-transfer/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/care/care-plans/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CARETAKER", "ROLE_UNDERWRITER", "ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/care/verifications/**").hasAnyAuthority("ROLE_CARETAKER", "ROLE_CLAIMS_OFFICER", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/care/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CARETAKER", "ROLE_ADMIN")

                        // Any other request must be authenticated
                        .anyExchange().authenticated()
                )
                .build();
    }

    private ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> {
            String token = (String) authentication.getCredentials();
            if (jwtUtil.isTokenValid(token)) {
                String role = jwtUtil.extractRole(token);
                String email = jwtUtil.extractEmail(token);
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                return Mono.just(new UsernamePasswordAuthenticationToken(email, token, authorities));
            }
            return Mono.empty();
        };
    }

    private ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
            }
            return Mono.empty();
        };
    }
}
