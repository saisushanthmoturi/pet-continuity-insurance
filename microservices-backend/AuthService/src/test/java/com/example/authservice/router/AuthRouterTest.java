package com.example.authservice.router;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.handler.AuthHandler;
import com.example.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthRouterTest {

    @Mock
    private AuthService authService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        AuthHandler handler = new AuthHandler(authService);
        AuthRouter router = new AuthRouter();
        RouterFunction<ServerResponse> routes = router.authRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void register_Success() {
        AuthResponse mockResponse = new AuthResponse("mock-jwt-token", 1L, "test@example.com", "Test User", "CUSTOMER");
        when(authService.register(any(RegisterRequest.class))).thenReturn(Mono.just(mockResponse));

        RegisterRequest request = new RegisterRequest("test@example.com", "Password123!", "Test User", "CUSTOMER");

        webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.token").isEqualTo("mock-jwt-token")
                .jsonPath("$.email").isEqualTo("test@example.com")
                .jsonPath("$.role").isEqualTo("CUSTOMER");
    }

    @Test
    void register_BadRequest_DuplicateEmail() {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Email is already registered: test@example.com")));

        RegisterRequest request = new RegisterRequest("test@example.com", "Password123!", "Test User", "CUSTOMER");

        webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Email is already registered: test@example.com");
    }

    @Test
    void login_Success() {
        AuthResponse mockResponse = new AuthResponse("mock-jwt-token", 1L, "test@example.com", "Test User", "CUSTOMER");
        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.just(mockResponse));

        LoginRequest request = new LoginRequest("test@example.com", "Password123!");

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("mock-jwt-token")
                .jsonPath("$.email").isEqualTo("test@example.com");
    }

    @Test
    void login_Unauthorized_InvalidCredentials() {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid email or password")));

        LoginRequest request = new LoginRequest("test@example.com", "WrongPassword");

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid email or password");
    }

    @Test
    void validate_ValidToken() {
        Map<String, Object> mockMap = Map.of("valid", true, "userId", 1L, "role", "CUSTOMER", "email", "test@example.com");
        when(authService.validateToken("mock-jwt-token")).thenReturn(Mono.just(mockMap));

        webTestClient.get()
                .uri("/api/auth/validate")
                .header("Authorization", "Bearer mock-jwt-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.valid").isEqualTo(true)
                .jsonPath("$.userId").isEqualTo(1)
                .jsonPath("$.role").isEqualTo("CUSTOMER");
    }
}
