package com.example.authservice.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "test-secret-key-with-sufficient-length-32bytes!";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET, 3600000); // 1 hour
    }

    @Test
    void generateToken_shouldProduceValidJwt() {
        String token = jwtUtil.generateToken(1L, "user@example.com", "CUSTOMER");
        assertNotNull(token);
        assertFalse(token.isBlank());

        assertTrue(jwtUtil.validateToken(token));

        Claims claims = jwtUtil.extractAllClaims(token);
        assertEquals("user@example.com", claims.getSubject());
        assertEquals(1L, ((Number) claims.get("userId")).longValue());
        assertEquals("CUSTOMER", claims.get("role"));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
        assertFalse(jwtUtil.validateToken(null));
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        JwtUtil expiredJwtUtil = new JwtUtil(TEST_SECRET, -1000);
        String expiredToken = expiredJwtUtil.generateToken(2L, "expired@example.com", "ADMIN");

        assertFalse(jwtUtil.validateToken(expiredToken));
    }
}
