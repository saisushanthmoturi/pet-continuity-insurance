package com.example.authservice.repository;

import com.example.authservice.model.RefreshToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken, Long> {
    Mono<RefreshToken> findByToken(String token);
    Flux<RefreshToken> findByUserId(Long userId);
}
