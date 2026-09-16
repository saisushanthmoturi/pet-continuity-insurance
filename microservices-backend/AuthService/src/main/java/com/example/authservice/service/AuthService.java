package com.example.authservice.service;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Mono<AuthResponse> register(RegisterRequest req) {
        if (req.email() == null || req.email().isBlank() || req.password() == null || req.password().isBlank()) {
            return Mono.error(new IllegalArgumentException("Email and password are required"));
        }
        String role = (req.role() == null || req.role().isBlank()) ? "CUSTOMER" : req.role().toUpperCase();

        return userRepository.findByEmail(req.email())
                .flatMap(existing -> Mono.<AuthResponse>error(new IllegalArgumentException("Email already registered: " + req.email())))
                .switchIfEmpty(Mono.defer(() -> {
                    String hashed = passwordEncoder.encode(req.password());
                    User user = User.createNew(req.email().trim().toLowerCase(), hashed, req.fullName(), role);
                    return userRepository.save(user)
                            .map(saved -> {
                                log.info("Registered user id={}, email={}, role={}", saved.getId(), saved.getEmail(), saved.getRole());
                                String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole());
                                return new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getFullName(), saved.getRole());
                            });
                }));
    }

    public Mono<AuthResponse> login(LoginRequest req) {
        if (req.email() == null || req.password() == null) {
            return Mono.error(new IllegalArgumentException("Email and password are required"));
        }
        return userRepository.findByEmail(req.email().trim().toLowerCase())
                .filter(u -> passwordEncoder.matches(req.password(), u.getPasswordHash()))
                .map(u -> {
                    log.info("Successful login for user id={}, email={}", u.getId(), u.getEmail());
                    String token = jwtUtil.generateToken(u.getId(), u.getEmail(), u.getRole());
                    return new AuthResponse(token, u.getId(), u.getEmail(), u.getFullName(), u.getRole());
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid email or password")));
    }

    public Mono<Map<String, Object>> validateToken(String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return Mono.error(new IllegalArgumentException("Invalid or expired token"));
        }
        var claims = jwtUtil.extractAllClaims(token);
        return Mono.just(Map.of(
                "valid", true,
                "userId", claims.get("userId"),
                "email", claims.get("email"),
                "role", claims.get("role")
        ));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<com.example.authservice.dto.UserDto> getAllUsers() {
        return userRepository.findAll()
                .map(u -> new com.example.authservice.dto.UserDto(u.getId(), u.getEmail(), u.getFullName(), u.getRole(), u.getCreatedAt()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<com.example.authservice.dto.UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found with id: " + id)))
                .map(u -> new com.example.authservice.dto.UserDto(u.getId(), u.getEmail(), u.getFullName(), u.getRole(), u.getCreatedAt()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<com.example.authservice.dto.UserDto> updateUser(Long id, com.example.authservice.dto.UserUpdateRequest req) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found with id: " + id)))
                .flatMap(u -> {
                    if (req.fullName() != null && !req.fullName().isBlank()) u.setFullName(req.fullName().trim());
                    if (req.role() != null && !req.role().isBlank()) u.setRole(req.role().toUpperCase().trim());
                    return userRepository.save(u);
                })
                .map(u -> new com.example.authservice.dto.UserDto(u.getId(), u.getEmail(), u.getFullName(), u.getRole(), u.getCreatedAt()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteUser(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found with id: " + id)))
                .flatMap(u -> userRepository.delete(u));
    }
}
