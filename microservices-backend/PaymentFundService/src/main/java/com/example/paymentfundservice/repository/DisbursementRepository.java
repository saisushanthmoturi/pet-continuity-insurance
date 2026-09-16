package com.example.paymentfundservice.repository;

import com.example.paymentfundservice.model.Disbursement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DisbursementRepository extends ReactiveCrudRepository<Disbursement, Long> {
    Flux<Disbursement> findByFundId(Long fundId);
    Flux<Disbursement> findByCaretakerId(Long caretakerId);
}
