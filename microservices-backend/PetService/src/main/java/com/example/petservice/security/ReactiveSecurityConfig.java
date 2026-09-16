package com.example.petservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ReactiveSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt(headerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            byte[] bytes = "{\"error\": \"Unauthorized: Authentication required\", \"status\": 401}".getBytes(StandardCharsets.UTF_8);
                            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                        })
                        .accessDeniedHandler((exchange, ex) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            byte[] bytes = "{\"error\": \"Forbidden: Access Denied\", \"status\": 403}".getBytes(StandardCharsets.UTF_8);
                            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                        })
                )
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .build();
    }

    @Bean
    public WebFilter headerAuthenticationFilter() {
        return (exchange, chain) -> {
            String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");
            String email = exchange.getRequest().getHeaders().getFirst("X-User-Email");
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

            List<GrantedAuthority> authorities = new ArrayList<>();
            if (role != null && !role.trim().isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()));
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"));
            }

            String principal = (email != null && !email.isBlank()) ? email : (userId != null ? userId : "system");
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        };
    }
}
