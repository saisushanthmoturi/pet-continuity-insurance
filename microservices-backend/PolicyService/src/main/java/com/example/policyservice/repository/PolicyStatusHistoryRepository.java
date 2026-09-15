package com.example.policyservice.repository;

import com.example.policyservice.model.PolicyStatusHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PolicyStatusHistoryRepository extends ReactiveCrudRepository<PolicyStatusHistory, Long> {
    Flux<PolicyStatusHistory> findByPolicyIdOrderByChangedAtDesc(Long policyId);
}
