package com.example.petservice.service;

import com.example.petservice.dto.MedicalRecordRequest;
import com.example.petservice.dto.PetRequest;
import com.example.petservice.model.Pet;
import com.example.petservice.model.PetMedicalRecord;
import com.example.petservice.repository.MedicalRecordRepository;
import com.example.petservice.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PetService {

    private static final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PetService(PetRepository petRepository, MedicalRecordRepository medicalRecordRepository) {
        this.petRepository = petRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Pet> createPet(PetRequest req) {
        if (req.customerId() == null || req.name() == null || req.species() == null || req.breed() == null || req.age() == null) {
            return Mono.error(new IllegalArgumentException("customerId, name, species, breed, and age are required"));
        }
        Pet pet = Pet.createNew(
                req.customerId(),
                req.name().trim(),
                req.species().trim(),
                req.breed().trim(),
                req.age(),
                req.weight(),
                req.gender(),
                req.estimatedAnnualCareCost()
        );
        return petRepository.save(pet)
                .doOnSuccess(p -> log.info("Registered pet id={}, name={}, breed={}, customerId={}", p.getId(), p.getName(), p.getBreed(), p.getCustomerId()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_CARETAKER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Pet> getPetById(Long id) {
        return petRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Pet not found with id: " + id)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_CARETAKER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Flux<Pet> getPetsByCustomerId(Long customerId) {
        return petRepository.findByCustomerId(customerId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<PetMedicalRecord> addMedicalRecord(Long petId, MedicalRecordRequest req) {
        if (req.conditionName() == null || req.conditionName().isBlank()) {
            return Mono.error(new IllegalArgumentException("conditionName is required"));
        }
        return petRepository.findById(petId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Pet not found with id: " + petId)))
                .flatMap(pet -> {
                    PetMedicalRecord record = PetMedicalRecord.createNew(
                            pet.getId(),
                            req.conditionName().trim(),
                            req.diagnosisDate(),
                            req.treatmentPlan(),
                            req.estimatedAnnualMedCost()
                    );
                    return medicalRecordRepository.save(record)
                            .doOnSuccess(r -> log.info("Added medical record id={} for petId={}, condition={}", r.getId(), r.getPetId(), r.getConditionName()));
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_CARETAKER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Flux<PetMedicalRecord> getMedicalRecordsByPetId(Long petId) {
        return medicalRecordRepository.findByPetId(petId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Pet> updatePet(Long id, PetRequest req) {
        return petRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Pet not found with id: " + id)))
                .flatMap(pet -> {
                    if (req.name() != null && !req.name().isBlank()) pet.setName(req.name());
                    if (req.species() != null && !req.species().isBlank()) pet.setSpecies(req.species());
                    if (req.breed() != null && !req.breed().isBlank()) pet.setBreed(req.breed());
                    if (req.age() != null) pet.setAge(req.age());
                    if (req.weight() != null) pet.setWeight(req.weight());
                    if (req.gender() != null) pet.setGender(req.gender());
                    if (req.estimatedAnnualCareCost() != null) pet.setEstimatedAnnualCareCost(req.estimatedAnnualCareCost());
                    return petRepository.save(pet)
                            .doOnSuccess(p -> log.info("Updated pet id={}, name={}", p.getId(), p.getName()));
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deletePet(Long id) {
        return petRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Pet not found with id: " + id)))
                .flatMap(petRepository::delete)
                .doOnSuccess(v -> log.info("Deleted pet id={}", id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<PetMedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_CARETAKER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<PetMedicalRecord> getMedicalRecordById(Long recordId) {
        return medicalRecordRepository.findById(recordId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Medical record not found with id: " + recordId)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<PetMedicalRecord> updateMedicalRecord(Long recordId, MedicalRecordRequest req) {
        return medicalRecordRepository.findById(recordId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Medical record not found with id: " + recordId)))
                .flatMap(rec -> {
                    if (req.conditionName() != null && !req.conditionName().isBlank()) rec.setConditionName(req.conditionName().trim());
                    if (req.diagnosisDate() != null) rec.setDiagnosisDate(req.diagnosisDate());
                    if (req.treatmentPlan() != null) rec.setTreatmentPlan(req.treatmentPlan());
                    if (req.estimatedAnnualMedCost() != null) rec.setEstimatedAnnualMedCost(req.estimatedAnnualMedCost());
                    return medicalRecordRepository.save(rec)
                            .doOnSuccess(r -> log.info("Updated medical record id={}", r.getId()));
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteMedicalRecord(Long recordId) {
        return medicalRecordRepository.findById(recordId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Medical record not found with id: " + recordId)))
                .flatMap(medicalRecordRepository::delete)
                .doOnSuccess(v -> log.info("Deleted medical record id={}", recordId));
    }
}
