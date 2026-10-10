package com.example.careverificationservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReactiveSecurityConfigTest {

    private ReactiveSecurityConfig config;
    private WebFilter filter;

    @BeforeEach
    void setUp() {
        config = new ReactiveSecurityConfig();
        filter = config.headerAuthenticationFilter();
    }

    @Test
    void securityWebFilterChain_buildsSuccessfully() {
        ServerHttpSecurity http = ServerHttpSecurity.http();
        SecurityWebFilterChain chain = config.securityWebFilterChain(http);
        assertNotNull(chain);
    }

    @Test
    void filter_withRoleAndEmail() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Role", "customer")
                .header("X-User-Email", "test@example.com")
                .header("X-User-Id", "10")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AtomicReference<Authentication> authRef = new AtomicReference<>();
        WebFilterChain chain = ex -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(authRef::set)
                .then();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertNotNull(authRef.get());
        assertEquals("test@example.com", authRef.get().getPrincipal());
        assertTrue(authRef.get().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
    }

    @Test
    void filter_withUserIdFallback() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Id", "99")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AtomicReference<Authentication> authRef = new AtomicReference<>();
        WebFilterChain chain = ex -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(authRef::set)
                .then();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertNotNull(authRef.get());
        assertEquals("99", authRef.get().getPrincipal());
        assertTrue(authRef.get().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_INTERNAL_SERVICE")));
    }

    @Test
    void filter_defaultSystemUser() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AtomicReference<Authentication> authRef = new AtomicReference<>();
        WebFilterChain chain = ex -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(authRef::set)
                .then();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertNotNull(authRef.get());
        assertEquals("system", authRef.get().getPrincipal());
        assertTrue(authRef.get().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_INTERNAL_SERVICE")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSecurityCustomHandlersViaReflection() throws Exception {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        HttpHeaders headers = new HttpHeaders();
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        when(exchange.getResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(headers);
        when(response.bufferFactory()).thenReturn(bufferFactory);
        when(response.writeWith(any())).thenReturn(Mono.empty());

        for (Method m : ReactiveSecurityConfig.class.getDeclaredMethods()) {
            if (m.getName().startsWith("lambda$") && m.getParameterCount() == 2) {
                m.setAccessible(true);
                Class<?> param2Type = m.getParameterTypes()[1];
                if (AccessDeniedException.class.isAssignableFrom(param2Type)) {
                    Object res = m.invoke(null, exchange, new AccessDeniedException("Access Denied"));
                    if (res instanceof Mono<?> mono) {
                        StepVerifier.create(mono).verifyComplete();
                    }
                } else if (AuthenticationException.class.isAssignableFrom(param2Type)) {
                    Object res = m.invoke(null, exchange, new BadCredentialsException("Auth error"));
                    if (res instanceof Mono<?> mono) {
                        StepVerifier.create(mono).verifyComplete();
                    }
                }
            }
        }
    }
}
