package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.Quote;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface QuoteRepository extends ReactiveCrudRepository<Quote, Long> {
    Flux<Quote> findByCustomerId(Long customerId);
    Flux<Quote> findByPetId(Long petId);
}
