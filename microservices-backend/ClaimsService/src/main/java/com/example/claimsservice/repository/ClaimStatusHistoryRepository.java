package com.example.claimsservice.repository;

import com.example.claimsservice.model.ClaimStatusHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ClaimStatusHistoryRepository extends ReactiveCrudRepository<ClaimStatusHistory, Long> {
    Flux<ClaimStatusHistory> findByClaimIdOrderByChangedAtDesc(Long claimId);
}
