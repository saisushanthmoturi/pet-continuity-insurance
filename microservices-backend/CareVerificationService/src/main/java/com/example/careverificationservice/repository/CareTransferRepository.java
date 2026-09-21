package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.CareTransfer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CareTransferRepository extends ReactiveCrudRepository<CareTransfer, Long> {
    Flux<CareTransfer> findByPetIdOrderByCreatedAtDesc(Long petId);
}
