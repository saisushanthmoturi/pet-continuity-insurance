package com.example.paymentfundservice.repository;

import com.example.paymentfundservice.model.PremiumPayment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<PremiumPayment, Long> {
    Flux<PremiumPayment> findByPolicyId(Long policyId);
}
