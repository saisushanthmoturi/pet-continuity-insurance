package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.VerificationHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface VerificationHistoryRepository extends ReactiveCrudRepository<VerificationHistory, Long> {
    Flux<VerificationHistory> findByPetId(Long petId);
    Flux<VerificationHistory> findByCaretakerId(Long caretakerId);
}
