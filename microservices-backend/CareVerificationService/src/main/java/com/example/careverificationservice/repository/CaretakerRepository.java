package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.Caretaker;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CaretakerRepository extends ReactiveCrudRepository<Caretaker, Long> {
    Flux<Caretaker> findByPetId(Long petId);
    Flux<Caretaker> findByCustomerId(Long customerId);
}
