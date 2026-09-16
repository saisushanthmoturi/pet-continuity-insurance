package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.CoverageAssessment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CoverageAssessmentRepository extends ReactiveCrudRepository<CoverageAssessment, Long> {
    Mono<CoverageAssessment> findByQuoteId(Long quoteId);
}
