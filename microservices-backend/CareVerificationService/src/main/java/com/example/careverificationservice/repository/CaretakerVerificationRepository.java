package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.CaretakerVerification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CaretakerVerificationRepository extends ReactiveCrudRepository<CaretakerVerification, Long> {
    Flux<CaretakerVerification> findByCaretakerIdOrderByVerifiedAtDesc(Long caretakerId);
}
