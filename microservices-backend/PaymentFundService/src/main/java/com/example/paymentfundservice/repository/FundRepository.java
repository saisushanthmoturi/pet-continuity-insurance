package com.example.paymentfundservice.repository;

import com.example.paymentfundservice.model.PetContinuityFund;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FundRepository extends ReactiveCrudRepository<PetContinuityFund, Long> {
    Mono<PetContinuityFund> findByPolicyId(Long policyId);
    Mono<PetContinuityFund> findByPetId(Long petId);
}
