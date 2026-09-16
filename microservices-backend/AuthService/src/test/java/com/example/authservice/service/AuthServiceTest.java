package com.example.authservice.service;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.dto.UserDto;
import com.example.authservice.dto.UserUpdateRequest;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Alice Johnson", "alice@example.com", "hashed_pwd", "CUSTOMER", "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void register_successful() {
        RegisterRequest req = new RegisterRequest("alice@example.com", "password123", "Alice Johnson", "CUSTOMER");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashed_pwd");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));
        when(jwtUtil.generateToken(1L, "alice@example.com", "CUSTOMER")).thenReturn("jwt.token.mock");

        StepVerifier.create(authService.register(req))
                .assertNext(response -> {
                    assertEquals("jwt.token.mock", response.token());
                    assertEquals(1L, response.userId());
                    assertEquals("alice@example.com", response.email());
                    assertEquals("Alice Johnson", response.fullName());
                    assertEquals("CUSTOMER", response.role());
                })
                .verifyComplete();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_failsWhenEmailOrPasswordBlank() {
        RegisterRequest reqNoEmail = new RegisterRequest("", "pwd", "Name", "CUSTOMER");
        StepVerifier.create(authService.register(reqNoEmail))
                .expectError(IllegalArgumentException.class)
                .verify();

        RegisterRequest reqNoPwd = new RegisterRequest("test@test.com", null, "Name", "CUSTOMER");
        StepVerifier.create(authService.register(reqNoPwd))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void register_failsWhenEmailAlreadyExists() {
        RegisterRequest req = new RegisterRequest("alice@example.com", "pwd", "Alice", "CUSTOMER");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authService.register(req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("already registered"))
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_successful() {
        LoginRequest req = new LoginRequest("alice@example.com", "password123");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.just(sampleUser));
        when(passwordEncoder.matches("password123", "hashed_pwd")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "alice@example.com", "CUSTOMER")).thenReturn("token123");

        StepVerifier.create(authService.login(req))
                .assertNext(res -> {
                    assertEquals("token123", res.token());
                    assertEquals("alice@example.com", res.email());
                })
                .verifyComplete();
    }

    @Test
    void login_invalidPassword_returnsError() {
        LoginRequest req = new LoginRequest("alice@example.com", "wrongpassword");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.just(sampleUser));
        when(passwordEncoder.matches("wrongpassword", "hashed_pwd")).thenReturn(false);

        StepVerifier.create(authService.login(req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Invalid email or password"))
                .verify();
    }

    @Test
    void login_userNotFound_returnsError() {
        LoginRequest req = new LoginRequest("nobody@example.com", "pwd");

        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Invalid email or password"))
                .verify();
    }

    @Test
    void validateToken_validToken_returnsClaimsMap() {
        String token = "valid.jwt.token";
        Claims mockClaims = mock(Claims.class);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractAllClaims(token)).thenReturn(mockClaims);
        when(mockClaims.get("userId")).thenReturn(1L);
        when(mockClaims.get("email")).thenReturn("alice@example.com");
        when(mockClaims.get("role")).thenReturn("CUSTOMER");

        StepVerifier.create(authService.validateToken(token))
                .assertNext(map -> {
                    assertEquals(true, map.get("valid"));
                    assertEquals(1L, map.get("userId"));
                    assertEquals("alice@example.com", map.get("email"));
                    assertEquals("CUSTOMER", map.get("role"));
                })
                .verifyComplete();
    }

    @Test
    void validateToken_invalidToken_returnsError() {
        when(jwtUtil.validateToken("bad.token")).thenReturn(false);

        StepVerifier.create(authService.validateToken("bad.token"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getUserById_found() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authService.getUserById(1L))
                .assertNext(dto -> {
                    assertEquals(1L, dto.id());
                    assertEquals("alice@example.com", dto.email());
                    assertEquals("Alice Johnson", dto.fullName());
                })
                .verifyComplete();
    }

    @Test
    void getUserById_notFound_returnsError() {
        when(userRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(authService.getUserById(99L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("not found"))
                .verify();
    }

    @Test
    void getAllUsers_returnsFlux() {
        User user2 = new User(2L, "Bob Smith", "bob@example.com", "hash", "ADMIN", "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now(), null);
        when(userRepository.findAll()).thenReturn(Flux.just(sampleUser, user2));

        StepVerifier.create(authService.getAllUsers())
                .expectNextMatches(u -> u.email().equals("alice@example.com"))
                .expectNextMatches(u -> u.email().equals("bob@example.com"))
                .verifyComplete();
    }

    @Test
    void updateUser_success() {
        UserUpdateRequest req = new UserUpdateRequest("Alice Updated", "ADMIN");
        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authService.updateUser(1L, req))
                .assertNext(dto -> {
                    assertEquals("Alice Updated", sampleUser.getFullName());
                    assertEquals("ADMIN", sampleUser.getRole());
                })
                .verifyComplete();
    }
}
