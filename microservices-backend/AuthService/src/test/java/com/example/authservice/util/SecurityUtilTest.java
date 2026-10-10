package com.example.authservice.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<SecurityUtil> constructor = SecurityUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void testGetRole_present() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "admin")
                .build();
        assertEquals("ADMIN", SecurityUtil.getRole(request));
    }

    @Test
    void testGetRole_absent() {
        MockServerRequest request = MockServerRequest.builder().build();
        assertEquals("ANONYMOUS", SecurityUtil.getRole(request));
    }

    @Test
    void testHasAnyRole_anonymous() {
        MockServerRequest request = MockServerRequest.builder().build();
        assertTrue(SecurityUtil.hasAnyRole(request, "ADMIN", "CUSTOMER"));
    }

    @Test
    void testHasAnyRole_internalService() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "INTERNAL_SERVICE")
                .build();
        assertTrue(SecurityUtil.hasAnyRole(request, "ADMIN"));
    }

    @Test
    void testHasAnyRole_matched() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "CUSTOMER")
                .build();
        assertTrue(SecurityUtil.hasAnyRole(request, "ADMIN", "CUSTOMER"));
    }

    @Test
    void testHasAnyRole_unmatched() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "USER")
                .build();
        assertFalse(SecurityUtil.hasAnyRole(request, "ADMIN", "CUSTOMER"));
    }

    @Test
    void testCheckRole_allowed() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .build();
        Mono<ServerResponse> response = SecurityUtil.checkRole(
                request,
                new String[]{"ADMIN"},
                () -> ServerResponse.ok().build()
        );
        StepVerifier.create(response)
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void testCheckRole_denied() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "USER")
                .build();
        Mono<ServerResponse> response = SecurityUtil.checkRole(
                request,
                new String[]{"ADMIN"},
                () -> ServerResponse.ok().build()
        );
        StepVerifier.create(response)
                .assertNext(res -> assertEquals(HttpStatus.FORBIDDEN, res.statusCode()))
                .verifyComplete();
    }
}
