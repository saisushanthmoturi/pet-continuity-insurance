package com.example.petservice.repository;

import com.example.petservice.model.PetMedicalRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MedicalRecordRepository extends ReactiveCrudRepository<PetMedicalRecord, Long> {
    Flux<PetMedicalRecord> findByPetId(Long petId);
}
