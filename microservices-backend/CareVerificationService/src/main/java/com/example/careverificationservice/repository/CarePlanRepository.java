package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.CarePlan;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CarePlanRepository extends ReactiveCrudRepository<CarePlan, Long> {
    Mono<CarePlan> findByPetId(Long petId);
}
