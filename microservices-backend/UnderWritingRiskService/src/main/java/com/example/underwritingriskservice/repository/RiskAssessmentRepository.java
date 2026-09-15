package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.RiskAssessment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RiskAssessmentRepository extends ReactiveCrudRepository<RiskAssessment, Long> {
    Mono<RiskAssessment> findByQuoteId(Long quoteId);
    Mono<RiskAssessment> findFirstByPetIdOrderByAssessmentDateDesc(Long petId);
}
