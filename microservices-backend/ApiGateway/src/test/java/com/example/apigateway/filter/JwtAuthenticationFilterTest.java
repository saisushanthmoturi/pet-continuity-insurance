package com.example.apigateway.filter;

import com.example.apigateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtil);
    }

    @Test
    void testGetOrder() {
        assertEquals(-100, filter.getOrder());
    }

    @Test
    void testFilter_optionsMethod_passesThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.method(HttpMethod.OPTIONS, "/api/claims").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        verify(chain).filter(exchange);
    }

    @Test
    void testFilter_publicEndpoint_passesThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        verify(chain).filter(exchange);
    }

    @Test
    void testFilter_missingToken_returnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/claims").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    void testFilter_invalidToken_returnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/claims")
                .header("Authorization", "Bearer invalidToken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.isTokenValid("invalidToken")).thenReturn(false);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    void testFilter_validCookieToken_injectsHeaders() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/claims")
                .cookie(new HttpCookie("jwt_token", "validCookieToken"))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims claims = mock(Claims.class);
        when(jwtUtil.isTokenValid("validCookieToken")).thenReturn(true);
        when(jwtUtil.extractAllClaims("validCookieToken")).thenReturn(claims);
        when(claims.get("userId")).thenReturn(5L);
        when(claims.get("role")).thenReturn("ADMIN");
        when(claims.getSubject()).thenReturn("admin@example.com");

        AtomicReference<ServerWebExchange> mutatedExchangeRef = new AtomicReference<>();
        when(chain.filter(any())).thenAnswer(inv -> {
            mutatedExchangeRef.set(inv.getArgument(0));
            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertNotNull(mutatedExchangeRef.get());
        assertEquals("5", mutatedExchangeRef.get().getRequest().getHeaders().getFirst("X-User-Id"));
        assertEquals("ADMIN", mutatedExchangeRef.get().getRequest().getHeaders().getFirst("X-User-Role"));
        assertEquals("admin@example.com", mutatedExchangeRef.get().getRequest().getHeaders().getFirst("X-User-Email"));
    }

    @Test
    void testFilter_validCookieToken_withNullClaims() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/claims")
                .cookie(new HttpCookie("jwt_token", "validCookieToken"))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims claims = mock(Claims.class);
        when(jwtUtil.isTokenValid("validCookieToken")).thenReturn(true);
        when(jwtUtil.extractAllClaims("validCookieToken")).thenReturn(claims);
        when(claims.get("userId")).thenReturn(null);
        when(claims.get("role")).thenReturn(null);
        when(claims.getSubject()).thenReturn(null);

        AtomicReference<ServerWebExchange> mutatedExchangeRef = new AtomicReference<>();
        when(chain.filter(any())).thenAnswer(inv -> {
            mutatedExchangeRef.set(inv.getArgument(0));
            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertNotNull(mutatedExchangeRef.get());
        assertEquals("", mutatedExchangeRef.get().getRequest().getHeaders().getFirst("X-User-Id"));
        assertEquals("", mutatedExchangeRef.get().getRequest().getHeaders().getFirst("X-User-Role"));
        assertEquals("", mutatedExchangeRef.get().getRequest().getHeaders().getFirst("X-User-Email"));
    }

    @Test
    void testFilter_validBearerToken_injectsHeaders() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/claims")
                .header("Authorization", "Bearer bearerToken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims claims = mock(Claims.class);
        when(jwtUtil.isTokenValid("bearerToken")).thenReturn(true);
        when(jwtUtil.extractAllClaims("bearerToken")).thenReturn(claims);
        when(claims.get("userId")).thenReturn(10L);
        when(claims.get("role")).thenReturn("CUSTOMER");
        when(claims.getSubject()).thenReturn("cust@example.com");

        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        verify(chain).filter(any());
    }

    @Test
    void testFilter_claimsExtractionThrowsException_returnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/claims")
                .header("Authorization", "Bearer bearerToken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.isTokenValid("bearerToken")).thenReturn(true);
        when(jwtUtil.extractAllClaims("bearerToken")).thenThrow(new RuntimeException("Corrupt token"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }
}
