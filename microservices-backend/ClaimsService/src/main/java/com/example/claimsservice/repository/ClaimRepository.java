package com.example.claimsservice.repository;

import com.example.claimsservice.model.Claim;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClaimRepository extends ReactiveCrudRepository<Claim, Long> {
    Flux<Claim> findByPolicyId(Long policyId);
    Mono<Claim> findByClaimNumber(String claimNumber);
}
