package com.example.petservice.repository;

import com.example.petservice.model.Pet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PetRepository extends ReactiveCrudRepository<Pet, Long> {
    Flux<Pet> findByCustomerId(Long customerId);
}
