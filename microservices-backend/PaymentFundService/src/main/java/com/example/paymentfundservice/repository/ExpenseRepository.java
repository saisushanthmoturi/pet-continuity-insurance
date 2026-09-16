package com.example.paymentfundservice.repository;

import com.example.paymentfundservice.model.Expense;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExpenseRepository extends ReactiveCrudRepository<Expense, Long> {
    Flux<Expense> findByFundId(Long fundId);
    Flux<Expense> findByCaretakerId(Long caretakerId);
}
