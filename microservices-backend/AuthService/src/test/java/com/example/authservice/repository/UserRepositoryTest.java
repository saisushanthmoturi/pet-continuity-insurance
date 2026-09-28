package com.example.authservice.repository;

import com.example.authservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.createNew("john.doe@example.com", "hashed_password", "John Doe", "CUSTOMER");
    }

    @Test
    void findByEmail_Found() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(userRepository.findByEmail("john.doe@example.com"))
                .expectNextMatches(user -> user.getEmail().equals("john.doe@example.com") && user.getRole().equals("CUSTOMER"))
                .verifyComplete();
    }

    @Test
    void findByEmail_NotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(userRepository.findByEmail("nonexistent@example.com"))
                .verifyComplete();
    }

    @Test
    void existsByEmail_True() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(Mono.just(true));

        StepVerifier.create(userRepository.existsByEmail("john.doe@example.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmail_False() {
        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(Mono.just(false));

        StepVerifier.create(userRepository.existsByEmail("unknown@example.com"))
                .expectNext(false)
                .verifyComplete();
    }
}
