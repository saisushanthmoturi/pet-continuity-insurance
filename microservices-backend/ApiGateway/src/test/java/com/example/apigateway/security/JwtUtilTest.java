package com.example.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private JwtUtil jwtUtil;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
        key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private String createToken(String subject, Map<String, Object> claims, long expiryMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMillis))
                .signWith(key)
                .compact();
    }

    @Test
    void testExtractAllClaims_and_isTokenValid_success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 100L);
        claims.put("role", "ADMIN");
        String token = createToken("admin@example.com", claims, 3600000);

        assertTrue(jwtUtil.isTokenValid(token));
        Claims extracted = jwtUtil.extractAllClaims(token);
        assertEquals("admin@example.com", extracted.getSubject());
        assertEquals("ADMIN", jwtUtil.extractRole(token));
        assertEquals("100", jwtUtil.extractUserId(token));
        assertEquals("admin@example.com", jwtUtil.extractEmail(token));
    }

    @Test
    void testIsTokenValid_expired() {
        Map<String, Object> claims = new HashMap<>();
        String expiredToken = createToken("user@example.com", claims, -1000);

        assertFalse(jwtUtil.isTokenValid(expiredToken));
    }

    @Test
    void testIsTokenValid_malformed() {
        assertFalse(jwtUtil.isTokenValid("not-a-valid-token"));
    }

    @Test
    void testExtractDefaults_whenClaimsMissing() {
        // Token without subject, but with email claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "fallback@example.com");
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(now))
                .expiration(new Date(now + 3600000))
                .signWith(key)
                .compact();

        assertEquals("CUSTOMER", jwtUtil.extractRole(token));
        assertEquals("", jwtUtil.extractUserId(token));
        assertEquals("fallback@example.com", jwtUtil.extractEmail(token));
    }
}
