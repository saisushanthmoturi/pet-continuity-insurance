package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.RatingRule;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RatingRuleRepository extends ReactiveCrudRepository<RatingRule, Long> {
    Flux<RatingRule> findByFactorAndStatus(String factor, String status);
    Flux<RatingRule> findByStatus(String status);
}
