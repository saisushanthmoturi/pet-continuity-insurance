package com.example.policyservice.repository;

import com.example.policyservice.model.Coverage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CoverageRepository extends ReactiveCrudRepository<Coverage, Long> {
    Flux<Coverage> findByPolicyId(Long policyId);
}
