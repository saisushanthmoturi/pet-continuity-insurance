package com.example.claimsservice.repository;

import com.example.claimsservice.model.ClaimInformationRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ClaimInformationRequestRepository extends ReactiveCrudRepository<ClaimInformationRequest, Long> {
    Flux<ClaimInformationRequest> findByClaimIdOrderByCreatedAtDesc(Long claimId);
}
