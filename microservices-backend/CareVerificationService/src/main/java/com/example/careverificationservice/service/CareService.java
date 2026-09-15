package com.example.careverificationservice.service;

import com.example.careverificationservice.dto.CarePlanRequest;
import com.example.careverificationservice.dto.CaretakerRequest;
import com.example.careverificationservice.dto.EligibilityResponse;
import com.example.careverificationservice.dto.VerificationRequest;
import com.example.careverificationservice.model.CarePlan;
import com.example.careverificationservice.model.Caretaker;
import com.example.careverificationservice.model.PetVerification;
import com.example.careverificationservice.repository.CarePlanRepository;
import com.example.careverificationservice.repository.CaretakerRepository;
import com.example.careverificationservice.repository.VerificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CareService {

    private static final Logger log = LoggerFactory.getLogger(CareService.class);

    private final CaretakerRepository caretakerRepository;
    private final CarePlanRepository carePlanRepository;
    private final VerificationRepository verificationRepository;

    public CareService(CaretakerRepository caretakerRepository,
                       CarePlanRepository carePlanRepository,
                       VerificationRepository verificationRepository) {
        this.caretakerRepository = caretakerRepository;
        this.carePlanRepository = carePlanRepository;
        this.verificationRepository = verificationRepository;
    }

    public Mono<Caretaker> addCaretaker(CaretakerRequest req) {
        if (req.customerId() == null || req.petId() == null || req.fullName() == null || req.phone() == null) {
            return Mono.error(new IllegalArgumentException("customerId, petId, fullName, and phone are required"));
        }
        Caretaker caretaker = Caretaker.create(
                req.customerId(),
                req.petId(),
                req.fullName().trim(),
                req.phone().trim(),
                req.email() != null ? req.email().trim() : "",
                req.caretakerType(),
                req.address()
        );
        return caretakerRepository.save(caretaker)
                .doOnSuccess(c -> log.info("Saved caretaker id={}, name={}, type={}, petId={}", c.getId(), c.getFullName(), c.getCaretakerType(), c.getPetId()));
    }

    public Flux<Caretaker> getCaretakersByPetId(Long petId) {
        return caretakerRepository.findByPetId(petId);
    }

    public Mono<Caretaker> updateCaretakerStatus(Long id, String status) {
        return caretakerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Caretaker not found with id: " + id)))
                .flatMap(c -> {
                    c.setStatus(status.toUpperCase());
                    return caretakerRepository.save(c)
                            .doOnSuccess(saved -> log.info("Updated caretaker id={} status to {}", saved.getId(), saved.getStatus()));
                });
    }

    public Mono<CarePlan> saveCarePlan(CarePlanRequest req) {
        if (req.petId() == null || req.primaryCaretakerId() == null) {
            return Mono.error(new IllegalArgumentException("petId and primaryCaretakerId are required"));
        }
        return carePlanRepository.findByPetId(req.petId())
                .flatMap(existing -> {
                    existing.setPrimaryCaretakerId(req.primaryCaretakerId());
                    existing.setBackupCaretakerId(req.backupCaretakerId());
                    existing.setVetContact(req.vetContact());
                    existing.setFeedingInstructions(req.feedingInstructions());
                    existing.setSpecialNeeds(req.specialNeeds());
                    return carePlanRepository.save(existing);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    CarePlan plan = CarePlan.create(
                            req.petId(),
                            req.primaryCaretakerId(),
                            req.backupCaretakerId(),
                            req.vetContact(),
                            req.feedingInstructions(),
                            req.specialNeeds()
                    );
                    return carePlanRepository.save(plan);
                }));
    }

    public Mono<CarePlan> getCarePlanByPetId(Long petId) {
        return carePlanRepository.findByPetId(petId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Care plan not found for petId: " + petId)));
    }

    public Mono<PetVerification> recordVerification(VerificationRequest req) {
        if (req.petId() == null || req.caretakerId() == null) {
            return Mono.error(new IllegalArgumentException("petId and caretakerId are required"));
        }
        PetVerification v = PetVerification.create(req.petId(), req.caretakerId(), req.verificationDate(), req.status(), req.notes());
        return verificationRepository.save(v)
                .doOnSuccess(saved -> log.info("Recorded pet verification id={}, petId={}, status={}", saved.getId(), saved.getPetId(), saved.getStatus()));
    }

    public Flux<PetVerification> getVerificationsByPetId(Long petId) {
        return verificationRepository.findByPetIdOrderByCreatedAtDesc(petId);
    }

    public Mono<EligibilityResponse> checkMonthlyEligibility(Long petId, Long caretakerId) {
        return caretakerRepository.findById(caretakerId)
                .switchIfEmpty(Mono.defer(() ->
                        caretakerRepository.findByPetId(petId).next()
                ))
                .flatMap(caretaker -> {
                    // Check caretaker status
                    if ("UNAVAILABLE".equalsIgnoreCase(caretaker.getStatus())) {
                        log.warn("Caretaker id={} is UNAVAILABLE, checking backup caretaker", caretaker.getId());
                        return carePlanRepository.findByPetId(petId)
                                .flatMap(plan -> {
                                    if (plan.getBackupCaretakerId() != null) {
                                        return caretakerRepository.findById(plan.getBackupCaretakerId())
                                                .filter(b -> "ACTIVE".equalsIgnoreCase(b.getStatus()))
                                                .flatMap(backup -> verifyPetCondition(petId, backup.getId(), "Primary caretaker unavailable; redirecting to backup caretaker " + backup.getFullName()))
                                                .switchIfEmpty(Mono.just(new EligibilityResponse(false, "Primary caretaker is unavailable and backup caretaker is not active", null)));
                                    }
                                    return Mono.just(new EligibilityResponse(false, "Primary caretaker is unavailable and no backup caretaker is assigned", null));
                                })
                                .defaultIfEmpty(new EligibilityResponse(false, "Primary caretaker is unavailable and no care plan found", null));
                    }

                    return verifyPetCondition(petId, caretaker.getId(), null);
                })
                .defaultIfEmpty(new EligibilityResponse(false, "No registered caretaker found for pet", null));
    }

    private Mono<EligibilityResponse> verifyPetCondition(Long petId, Long activeCaretakerId, String transferNote) {
        return verificationRepository.findFirstByPetIdOrderByCreatedAtDesc(petId)
                .map(v -> {
                    if ("FAILED".equalsIgnoreCase(v.getStatus())) {
                        return new EligibilityResponse(false, "Pet welfare verification FAILED on " + v.getVerificationDate() + ": " + v.getNotes(), activeCaretakerId);
                    }
                    String note = transferNote != null ? transferNote : "Pet and caretaker verified successfully";
                    return new EligibilityResponse(true, note, activeCaretakerId);
                })
                .defaultIfEmpty(new EligibilityResponse(true, transferNote != null ? transferNote : "Pet check approved (initial cycle)", activeCaretakerId));
    }
}
