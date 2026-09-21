package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.PremiumCalculation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PremiumCalculationRepository extends ReactiveCrudRepository<PremiumCalculation, Long> {
    Flux<PremiumCalculation> findByQuoteId(Long quoteId);
    Mono<PremiumCalculation> findFirstByQuoteIdOrderByCalculatedAtDesc(Long quoteId);
}
