package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.Quote;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface QuoteRepository extends ReactiveCrudRepository<Quote, Long> {

    @Query("SELECT * FROM quotes WHERE customer_id = :customerId ORDER BY created_at DESC")
    Flux<Quote> findByCustomerId(Long customerId);

    @Query("SELECT * FROM quotes WHERE pet_id = :petId ORDER BY created_at DESC")
    Flux<Quote> findByPetId(Long petId);
}
