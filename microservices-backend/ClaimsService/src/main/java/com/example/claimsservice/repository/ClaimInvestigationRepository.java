package com.example.claimsservice.repository;

import com.example.claimsservice.model.ClaimInvestigation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ClaimInvestigationRepository extends ReactiveCrudRepository<ClaimInvestigation, Long> {
    Mono<ClaimInvestigation> findByClaimId(Long claimId);
}
