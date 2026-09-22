package com.example.paymentfundservice.repository;

import com.example.paymentfundservice.model.PremiumPayment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<PremiumPayment, Long> {
    Flux<PremiumPayment> findByPolicyId(Long policyId);

    @Query("SELECT * FROM premium_payments WHERE customer_id = :customerId ORDER BY payment_date DESC")
    Flux<PremiumPayment> findByCustomerId(Long customerId);
}
