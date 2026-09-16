package com.example.claimsservice.repository;

import com.example.claimsservice.model.FraudAssessment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FraudAssessmentRepository extends ReactiveCrudRepository<FraudAssessment, Long> {
    Mono<FraudAssessment> findByClaimId(Long claimId);
}
