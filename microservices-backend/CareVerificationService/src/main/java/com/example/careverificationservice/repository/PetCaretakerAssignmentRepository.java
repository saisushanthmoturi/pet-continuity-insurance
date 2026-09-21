package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.PetCaretakerAssignment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PetCaretakerAssignmentRepository extends ReactiveCrudRepository<PetCaretakerAssignment, Long> {
    Flux<PetCaretakerAssignment> findByPetId(Long petId);
    Flux<PetCaretakerAssignment> findByCaretakerId(Long caretakerId);
}
