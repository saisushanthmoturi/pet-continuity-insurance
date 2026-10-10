package com.example.claimsservice.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    @Test
    void getRole_withHeader() {
        MockServerRequest req = MockServerRequest.builder().header("X-User-Role", "ADMIN").build();
        assertEquals("ADMIN", SecurityUtil.getRole(req));
    }

    @Test
    void getRole_withoutHeader_returnsAnonymous() {
        MockServerRequest req = MockServerRequest.builder().build();
        assertEquals("ANONYMOUS", SecurityUtil.getRole(req));
    }

    @Test
    void hasAnyRole_anonymousOrInternal_returnsTrue() {
        MockServerRequest reqAnon = MockServerRequest.builder().build();
        assertTrue(SecurityUtil.hasAnyRole(reqAnon, "ADMIN"));

        MockServerRequest reqInternal = MockServerRequest.builder().header("X-User-Role", "INTERNAL_SERVICE").build();
        assertTrue(SecurityUtil.hasAnyRole(reqInternal, "ADMIN"));
    }

    @Test
    void hasAnyRole_matchingRole_returnsTrue() {
        MockServerRequest req = MockServerRequest.builder().header("X-User-Role", "CLAIMS_OFFICER").build();
        assertTrue(SecurityUtil.hasAnyRole(req, "ADMIN", "CLAIMS_OFFICER"));
    }

    @Test
    void hasAnyRole_nonMatchingRole_returnsFalse() {
        MockServerRequest req = MockServerRequest.builder().header("X-User-Role", "CUSTOMER").build();
        assertFalse(SecurityUtil.hasAnyRole(req, "ADMIN", "CLAIMS_OFFICER"));
    }

    @Test
    void checkRole_allowedRole_executesAction() {
        MockServerRequest req = MockServerRequest.builder().header("X-User-Role", "ADMIN").build();
        Mono<ServerResponse> result = SecurityUtil.checkRole(req, new String[]{"ADMIN"}, () -> ServerResponse.ok().build());

        StepVerifier.create(result)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void checkRole_disallowedRole_returnsForbidden() {
        MockServerRequest req = MockServerRequest.builder().header("X-User-Role", "CUSTOMER").build();
        Mono<ServerResponse> result = SecurityUtil.checkRole(req, new String[]{"ADMIN"}, () -> ServerResponse.ok().build());

        StepVerifier.create(result)
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }
}
