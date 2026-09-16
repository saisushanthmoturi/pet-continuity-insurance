package com.example.claimsservice.repository;

import com.example.claimsservice.model.EventVerification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EventVerificationRepository extends ReactiveCrudRepository<EventVerification, Long> {
    Flux<EventVerification> findByClaimId(Long claimId);
}
