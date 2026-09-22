package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.RiskAssessment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RiskAssessmentRepository extends ReactiveCrudRepository<RiskAssessment, Long> {

    @Query("SELECT * FROM risk_assessments WHERE quote_id = :quoteId ORDER BY assessed_at DESC, risk_assessment_id DESC LIMIT 1")
    Mono<RiskAssessment> findByQuoteId(Long quoteId);

    @Query("SELECT ra.* FROM risk_assessments ra JOIN quotes q ON ra.quote_id = q.quote_id WHERE q.pet_id = :petId ORDER BY ra.assessed_at DESC LIMIT 1")
    Mono<RiskAssessment> findFirstByPetIdOrderByAssessmentDateDesc(Long petId);
}
