package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.RiskAssessment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RiskAssessmentRepository extends ReactiveCrudRepository<RiskAssessment, Long> {

    Mono<RiskAssessment> findByQuoteId(Long quoteId);

    @Query("SELECT ra.* FROM risk_assessments ra JOIN quotes q ON ra.quote_id = q.quote_id WHERE q.pet_id = :petId ORDER BY ra.assessed_at DESC LIMIT 1")
    Mono<RiskAssessment> findFirstByPetIdOrderByAssessmentDateDesc(Long petId);
}
