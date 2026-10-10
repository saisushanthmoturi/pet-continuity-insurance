package com.example.paymentfundservice.util;

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
    void privateConstructor_isPrivate() throws Exception {
        Constructor<SecurityUtil> constructor = SecurityUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        SecurityUtil instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void getRole_returnsUpperRoleWhenHeaderPresent() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "customer")
                .build();
        assertEquals("CUSTOMER", SecurityUtil.getRole(request));
    }

    @Test
    void getRole_returnsAnonymousWhenHeaderAbsent() {
        MockServerRequest request = MockServerRequest.builder().build();
        assertEquals("ANONYMOUS", SecurityUtil.getRole(request));
    }

    @Test
    void hasAnyRole_anonymousOrInternalService_returnsTrue() {
        MockServerRequest anon = MockServerRequest.builder().build();
        assertTrue(SecurityUtil.hasAnyRole(anon, "ADMIN"));

        MockServerRequest internal = MockServerRequest.builder()
                .header("X-User-Role", "INTERNAL_SERVICE")
                .build();
        assertTrue(SecurityUtil.hasAnyRole(internal, "ADMIN"));
    }

    @Test
    void hasAnyRole_matchingRole_returnsTrue() {
        MockServerRequest req = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .build();
        assertTrue(SecurityUtil.hasAnyRole(req, "ADMIN", "CUSTOMER"));
    }

    @Test
    void hasAnyRole_nonMatchingRole_returnsFalse() {
        MockServerRequest req = MockServerRequest.builder()
                .header("X-User-Role", "CUSTOMER")
                .build();
        assertFalse(SecurityUtil.hasAnyRole(req, "ADMIN", "CARETAKER"));
    }

    @Test
    void checkRole_allowed_executesAction() {
        MockServerRequest req = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .build();
        Mono<ServerResponse> response = SecurityUtil.checkRole(req, new String[]{"ADMIN"}, () -> ServerResponse.ok().build());

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void checkRole_denied_returnsForbidden() {
        MockServerRequest req = MockServerRequest.builder()
                .header("X-User-Role", "CUSTOMER")
                .build();
        Mono<ServerResponse> response = SecurityUtil.checkRole(req, new String[]{"ADMIN"}, () -> ServerResponse.ok().build());

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }
}
