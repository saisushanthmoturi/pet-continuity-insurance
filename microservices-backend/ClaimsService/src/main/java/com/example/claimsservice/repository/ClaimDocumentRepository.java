package com.example.claimsservice.repository;

import com.example.claimsservice.model.ClaimDocument;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ClaimDocumentRepository extends ReactiveCrudRepository<ClaimDocument, Long> {
    Flux<ClaimDocument> findByClaimId(Long claimId);
}
