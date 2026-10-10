package com.example.apigateway.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FallbackHandlerTest {

    @Test
    void testServiceFallback() {
        FallbackHandler handler = new FallbackHandler();
        StepVerifier.create(handler.serviceFallback("claims-service"))
                .assertNext(map -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), map.get("status"));
                    assertEquals("Service Unavailable", map.get("error"));
                    assertTrue(((String) map.get("message")).contains("claims-service"));
                    assertEquals(true, map.get("fallback"));
                    assertNotNull(map.get("timestamp"));
                })
                .verifyComplete();
    }
}
