package com.example.paymentfundservice.repository;

import com.example.paymentfundservice.model.FundStatusHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FundStatusHistoryRepository extends ReactiveCrudRepository<FundStatusHistory, Long> {
    Flux<FundStatusHistory> findByFundIdOrderByChangedAtDesc(Long fundId);
}
