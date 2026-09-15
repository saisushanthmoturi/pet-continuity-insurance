package com.example.paymentfundservice.repository;

import com.example.paymentfundservice.model.FundTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<FundTransaction, Long> {
    Flux<FundTransaction> findByFundIdOrderByCreatedAtDesc(Long fundId);
}
