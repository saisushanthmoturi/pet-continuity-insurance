package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.MonthlyEligibleCheck;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MonthlyEligibleCheckRepository extends ReactiveCrudRepository<MonthlyEligibleCheck, Long> {
    Flux<MonthlyEligibleCheck> findByPetId(Long petId);
    Mono<MonthlyEligibleCheck> findByPetIdAndEligibilityMonth(Long petId, String eligibilityMonth);
}
