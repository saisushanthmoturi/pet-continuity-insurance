package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.PetVerification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VerificationRepository extends ReactiveCrudRepository<PetVerification, Long> {
    Flux<PetVerification> findByPetIdOrderByCreatedAtDesc(Long petId);
    Mono<PetVerification> findFirstByPetIdOrderByCreatedAtDesc(Long petId);
}
