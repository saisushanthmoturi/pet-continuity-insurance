package com.example.authservice.handler;

import com.example.authservice.dto.*;
import com.example.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private AuthService authService;

    private AuthHandler handler;
    private AuthResponse sampleAuthResponse;
    private UserDto sampleUserDto;

    @BeforeEach
    void setUp() {
        handler = new AuthHandler(authService);
        sampleAuthResponse = new AuthResponse("token123", 1L, "test@example.com", "Test User", "CUSTOMER");
        sampleUserDto = new UserDto(1L, "test@example.com", "Test User", "CUSTOMER", LocalDateTime.now());
    }

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest("test@example.com", "pass123", "Test User", "CUSTOMER");
        when(authService.register(any(RegisterRequest.class))).thenReturn(Mono.just(sampleAuthResponse));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        StepVerifier.create(handler.register(request))
                .assertNext(res -> {
                    assertEquals(HttpStatus.CREATED, res.statusCode());
                    assertNotNull(res.cookies().getFirst("jwt_token"));
                })
                .verifyComplete();
    }

    @Test
    void register_error() {
        when(authService.register(any(RegisterRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Email exists")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new RegisterRequest(null, null, null, null)));
        StepVerifier.create(handler.register(request))
                .assertNext(res -> assertEquals(HttpStatus.BAD_REQUEST, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest("test@example.com", "pass123");
        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.just(sampleAuthResponse));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        StepVerifier.create(handler.login(request))
                .assertNext(res -> {
                    assertEquals(HttpStatus.OK, res.statusCode());
                    assertNotNull(res.cookies().getFirst("jwt_token"));
                })
                .verifyComplete();
    }

    @Test
    void login_error() {
        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Bad credentials")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new LoginRequest("a", "b")));
        StepVerifier.create(handler.login(request))
                .assertNext(res -> assertEquals(HttpStatus.UNAUTHORIZED, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void logout_success() {
        MockServerRequest request = MockServerRequest.builder().build();
        StepVerifier.create(handler.logout(request))
                .assertNext(res -> {
                    assertEquals(HttpStatus.OK, res.statusCode());
                    assertEquals(0, res.cookies().getFirst("jwt_token").getMaxAge().getSeconds());
                })
                .verifyComplete();
    }

    @Test
    void validate_withCookie_success() {
        Map<String, Object> claims = Map.of("userId", 1L, "email", "test@example.com", "role", "CUSTOMER");
        when(authService.validateToken("cookieToken")).thenReturn(Mono.just(claims));

        MockServerRequest request = MockServerRequest.builder()
                .cookie(new HttpCookie("jwt_token", "cookieToken"))
                .build();

        StepVerifier.create(handler.validate(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void validate_withBearerHeader_success() {
        Map<String, Object> claims = Map.of("userId", 1L, "email", "test@example.com", "role", "CUSTOMER");
        when(authService.validateToken("headerToken")).thenReturn(Mono.just(claims));

        MockServerRequest request = MockServerRequest.builder()
                .header("Authorization", "Bearer headerToken")
                .build();

        StepVerifier.create(handler.validate(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void validate_missingToken() {
        MockServerRequest request = MockServerRequest.builder().build();

        StepVerifier.create(handler.validate(request))
                .assertNext(res -> assertEquals(HttpStatus.UNAUTHORIZED, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void validate_invalidToken() {
        when(authService.validateToken("badToken")).thenReturn(Mono.error(new IllegalArgumentException("Invalid token")));

        MockServerRequest request = MockServerRequest.builder()
                .header("Authorization", "Bearer badToken")
                .build();

        StepVerifier.create(handler.validate(request))
                .assertNext(res -> assertEquals(HttpStatus.UNAUTHORIZED, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllUsers_allowed() {
        when(authService.getAllUsers()).thenReturn(Flux.just(sampleUserDto));

        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .build();

        StepVerifier.create(handler.getAllUsers(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllUsers_denied() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "CUSTOMER")
                .build();

        StepVerifier.create(handler.getAllUsers(request))
                .assertNext(res -> assertEquals(HttpStatus.FORBIDDEN, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getUserById_allowed_found() {
        when(authService.getUserById(1L)).thenReturn(Mono.just(sampleUserDto));

        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .pathVariable("id", "1")
                .build();

        StepVerifier.create(handler.getUserById(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getUserById_allowed_notFound() {
        when(authService.getUserById(99L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .pathVariable("id", "99")
                .build();

        StepVerifier.create(handler.getUserById(request))
                .assertNext(res -> assertEquals(HttpStatus.NOT_FOUND, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getUserById_denied() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "USER")
                .pathVariable("id", "1")
                .build();

        StepVerifier.create(handler.getUserById(request))
                .assertNext(res -> assertEquals(HttpStatus.FORBIDDEN, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateUser_allowed_success() {
        UserUpdateRequest req = new UserUpdateRequest("New Name", "CARETAKER");
        when(authService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(Mono.just(sampleUserDto));

        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(handler.updateUser(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateUser_allowed_error() {
        when(authService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Bad req")));

        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .pathVariable("id", "1")
                .body(Mono.just(new UserUpdateRequest("", "")));

        StepVerifier.create(handler.updateUser(request))
                .assertNext(res -> assertEquals(HttpStatus.BAD_REQUEST, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateUser_denied() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "CUSTOMER")
                .pathVariable("id", "1")
                .body(Mono.just(new UserUpdateRequest("", "")));

        StepVerifier.create(handler.updateUser(request))
                .assertNext(res -> assertEquals(HttpStatus.FORBIDDEN, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteUser_allowed_success() {
        when(authService.deleteUser(1L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .pathVariable("id", "1")
                .build();

        StepVerifier.create(handler.deleteUser(request))
                .assertNext(res -> assertEquals(HttpStatus.NO_CONTENT, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteUser_allowed_notFound() {
        when(authService.deleteUser(99L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "ADMIN")
                .pathVariable("id", "99")
                .build();

        StepVerifier.create(handler.deleteUser(request))
                .assertNext(res -> assertEquals(HttpStatus.NOT_FOUND, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteUser_denied() {
        MockServerRequest request = MockServerRequest.builder()
                .header("X-User-Role", "CUSTOMER")
                .pathVariable("id", "1")
                .build();

        StepVerifier.create(handler.deleteUser(request))
                .assertNext(res -> assertEquals(HttpStatus.FORBIDDEN, res.statusCode()))
                .verifyComplete();
    }
}
