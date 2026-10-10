package com.example.authservice.service;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.dto.UserUpdateRequest;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private AuthService authService;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);

        sampleUser = new User(1L, "alice_johnson", "alice@example.com", "hashedPassword",
                "CUSTOMER", "ACTIVE", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest("alice@example.com", "secret123", "Alice Johnson", "CUSTOMER");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));
        when(jwtUtil.generateToken(1L, "alice@example.com", "CUSTOMER")).thenReturn("jwt.token.here");

        StepVerifier.create(authService.register(req))
                .assertNext(res -> {
                    assertEquals("jwt.token.here", res.token());
                    assertEquals(1L, res.userId());
                    assertEquals("alice@example.com", res.email());
                    assertEquals("CUSTOMER", res.role());
                })
                .verifyComplete();
    }

    @Test
    void register_defaultRoleWhenNullOrBlank() {
        RegisterRequest req = new RegisterRequest("alice@example.com", "secret123", "Alice Johnson", "  ");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));
        when(jwtUtil.generateToken(1L, "alice@example.com", "CUSTOMER")).thenReturn("jwt.token.here");

        StepVerifier.create(authService.register(req))
                .assertNext(res -> assertEquals("CUSTOMER", res.role()))
                .verifyComplete();
    }

    @Test
    void register_missingEmailOrPassword_throwsError() {
        RegisterRequest noEmail = new RegisterRequest(null, "pass", "Name", "CUSTOMER");
        StepVerifier.create(authService.register(noEmail)).expectError(IllegalArgumentException.class).verify();

        RegisterRequest blankEmail = new RegisterRequest("  ", "pass", "Name", "CUSTOMER");
        StepVerifier.create(authService.register(blankEmail)).expectError(IllegalArgumentException.class).verify();

        RegisterRequest noPass = new RegisterRequest("email@example.com", null, "Name", "CUSTOMER");
        StepVerifier.create(authService.register(noPass)).expectError(IllegalArgumentException.class).verify();

        RegisterRequest blankPass = new RegisterRequest("email@example.com", "  ", "Name", "CUSTOMER");
        StepVerifier.create(authService.register(blankPass)).expectError(IllegalArgumentException.class).verify();
    }

    @Test
    void register_duplicateEmail_throwsError() {
        RegisterRequest req = new RegisterRequest("alice@example.com", "secret123", "Alice Johnson", "CUSTOMER");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authService.register(req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("already registered"))
                .verify();
    }

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest("alice@example.com", "secret123");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.just(sampleUser));
        when(passwordEncoder.matches("secret123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "alice@example.com", "CUSTOMER")).thenReturn("jwt.token.here");

        StepVerifier.create(authService.login(req))
                .assertNext(res -> {
                    assertEquals("jwt.token.here", res.token());
                    assertEquals(1L, res.userId());
                })
                .verifyComplete();
    }

    @Test
    void login_missingEmailOrPassword_throwsError() {
        LoginRequest noEmail = new LoginRequest(null, "secret");
        StepVerifier.create(authService.login(noEmail)).expectError(IllegalArgumentException.class).verify();

        LoginRequest noPass = new LoginRequest("email@example.com", null);
        StepVerifier.create(authService.login(noPass)).expectError(IllegalArgumentException.class).verify();
    }

    @Test
    void login_invalidPassword_throwsError() {
        LoginRequest req = new LoginRequest("alice@example.com", "wrongPassword");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.just(sampleUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        StepVerifier.create(authService.login(req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Invalid email or password"))
                .verify();
    }

    @Test
    void login_userNotFound_throwsError() {
        LoginRequest req = new LoginRequest("nobody@example.com", "password");
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Invalid email or password"))
                .verify();
    }

    @Test
    void validateToken_validToken_returnsClaims() {
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
    void validateToken_nullOrInvalidToken_returnsError() {
        StepVerifier.create(authService.validateToken(null))
                .expectError(IllegalArgumentException.class)
                .verify();

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
                    assertEquals("alice_johnson", dto.fullName());
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
        User user2 = new User(2L, "bob_smith", "bob@example.com", "hash", "ADMIN", "ACTIVE",
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

    @Test
    void updateUser_partialNulls() {
        UserUpdateRequest req = new UserUpdateRequest(null, "  ");
        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authService.updateUser(1L, req))
                .assertNext(dto -> assertEquals("alice_johnson", sampleUser.getFullName()))
                .verifyComplete();
    }

    @Test
    void updateUser_notFound_throwsError() {
        when(userRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(authService.updateUser(99L, new UserUpdateRequest("Name", "ROLE")))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));
        when(userRepository.delete(sampleUser)).thenReturn(Mono.empty());

        StepVerifier.create(authService.deleteUser(1L))
                .verifyComplete();

        verify(userRepository).delete(sampleUser);
    }

    @Test
    void deleteUser_notFound_throwsError() {
        when(userRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(authService.deleteUser(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
