package com.example.policyservice.repository;

import com.example.policyservice.model.Policy;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PolicyRepository extends ReactiveCrudRepository<Policy, Long> {
    Flux<Policy> findByCustomerId(Long customerId);
    Flux<Policy> findByPetId(Long petId);
    Mono<Policy> findByPolicyNumber(String policyNumber);
    Mono<Policy> findByQuoteId(Long quoteId);
}
