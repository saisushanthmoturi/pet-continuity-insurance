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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.example.careverificationservice.model.CareTransfer;
import com.example.careverificationservice.model.CaretakerVerification;
import com.example.careverificationservice.repository.CareTransferRepository;
import com.example.careverificationservice.repository.CaretakerVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CareService {

    private static final Logger log = LoggerFactory.getLogger(CareService.class);

    private final CaretakerRepository caretakerRepository;
    private final CarePlanRepository carePlanRepository;
    private final VerificationRepository verificationRepository;
    private final CareTransferRepository careTransferRepository;
    private final CaretakerVerificationRepository caretakerVerificationRepository;

    @Autowired
    public CareService(CaretakerRepository caretakerRepository,
                       CarePlanRepository carePlanRepository,
                       VerificationRepository verificationRepository,
                       CareTransferRepository careTransferRepository,
                       CaretakerVerificationRepository caretakerVerificationRepository) {
        this.caretakerRepository = caretakerRepository;
        this.carePlanRepository = carePlanRepository;
        this.verificationRepository = verificationRepository;
        this.careTransferRepository = careTransferRepository;
        this.caretakerVerificationRepository = caretakerVerificationRepository;
    }

    public CareService(CaretakerRepository caretakerRepository,
                       CarePlanRepository carePlanRepository,
                       VerificationRepository verificationRepository) {
        this(caretakerRepository, carePlanRepository, verificationRepository, null, null);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<Caretaker> getAllCaretakers() {
        return caretakerRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CARETAKER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Caretaker> getCaretakerById(Long id) {
        return caretakerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Caretaker not found with id: " + id)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Mono<Caretaker> updateCaretaker(Long id, CaretakerRequest req) {
        return caretakerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Caretaker not found with id: " + id)))
                .flatMap(c -> {
                    if (req.fullName() != null) c.setFullName(req.fullName().trim());
                    if (req.phone() != null) c.setPhone(req.phone().trim());
                    if (req.email() != null) c.setEmail(req.email().trim());
                    if (req.caretakerType() != null) c.setCaretakerType(req.caretakerType().toUpperCase());
                    if (req.address() != null) c.setAddress(req.address());
                    return caretakerRepository.save(c);
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteCaretaker(Long id) {
        return caretakerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Caretaker not found with id: " + id)))
                .flatMap(c -> caretakerRepository.delete(c));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CARETAKER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Flux<Caretaker> getCaretakersByPetId(Long petId) {
        return caretakerRepository.findByPetId(petId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CARETAKER', 'ROLE_ADMIN')")
    public Mono<Caretaker> updateCaretakerStatus(Long id, String status) {
        return caretakerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Caretaker not found with id: " + id)))
                .flatMap(c -> {
                    c.setStatus(status.toUpperCase());
                    return caretakerRepository.save(c)
                            .doOnSuccess(saved -> log.info("Updated caretaker id={} status to {}", saved.getId(), saved.getStatus()));
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Caretaker> transferToBackup(Long petId) {
        return carePlanRepository.findByPetId(petId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No care plan found for pet " + petId)))
                .flatMap(plan -> {
                    if (plan.getBackupCaretakerId() == null) {
                        return Mono.error(new IllegalStateException("No backup caretaker defined in care plan"));
                    }
                    return caretakerRepository.findById(plan.getPrimaryCaretakerId())
                            .flatMap(primary -> {
                                primary.setStatus("UNAVAILABLE");
                                return caretakerRepository.save(primary);
                            })
                            .then(caretakerRepository.findById(plan.getBackupCaretakerId())
                                    .flatMap(backup -> {
                                        backup.setCaretakerType("PRIMARY");
                                        backup.setStatus("ACTIVE");
                                        return caretakerRepository.save(backup)
                                                .flatMap(savedBackup -> {
                                                    if (careTransferRepository != null) {
                                                        CareTransfer transfer = new CareTransfer(
                                                                null, petId, plan.getPrimaryCaretakerId(), savedBackup.getId(),
                                                                "Primary caretaker became unavailable", java.time.LocalDateTime.now(),
                                                                "SYSTEM", "COMPLETED", java.time.LocalDateTime.now()
                                                        );
                                                        return careTransferRepository.save(transfer).thenReturn(savedBackup);
                                                    }
                                                    return Mono.just(savedBackup);
                                                });
                                    }));
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<CarePlan> getAllCarePlans() {
        return carePlanRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CARETAKER', 'ROLE_ADMIN')")
    public Mono<CarePlan> getCarePlanById(Long id) {
        return carePlanRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Care plan not found with id: " + id)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteCarePlan(Long id) {
        return carePlanRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Care plan not found with id: " + id)))
                .flatMap(p -> carePlanRepository.delete(p));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CARETAKER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<CarePlan> getCarePlanByPetId(Long petId) {
        return carePlanRepository.findByPetId(petId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Care plan not found for petId: " + petId)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<PetVerification> getAllVerifications() {
        return verificationRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Mono<PetVerification> getVerificationById(Long id) {
        return verificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Verification record not found with id: " + id)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteVerification(Long id) {
        return verificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Verification record not found with id: " + id)))
                .flatMap(v -> verificationRepository.delete(v));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_ADMIN')")
    public Mono<PetVerification> recordVerification(VerificationRequest req) {
        if (req.petId() == null || req.caretakerId() == null) {
            return Mono.error(new IllegalArgumentException("petId and caretakerId are required"));
        }
        PetVerification v = PetVerification.create(req.petId(), req.caretakerId(), req.verificationDate(), req.status(), req.notes());
        return verificationRepository.save(v)
                .doOnSuccess(saved -> log.info("Recorded pet verification id={}, petId={}, status={}", saved.getId(), saved.getPetId(), saved.getStatus()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Flux<PetVerification> getVerificationsByPetId(Long petId) {
        return verificationRepository.findByPetIdOrderByCreatedAtDesc(petId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
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

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CARETAKER', 'ROLE_ADMIN')")
    public Flux<CareTransfer> getTransfersByPetId(Long petId) {
        return careTransferRepository != null ? careTransferRepository.findByPetIdOrderByCreatedAtDesc(petId) : Flux.empty();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_ADMIN')")
    public Mono<CaretakerVerification> recordCaretakerVerification(Long caretakerId, CaretakerVerification v) {
        if (caretakerVerificationRepository == null) return Mono.empty();
        v.setCaretakerId(caretakerId);
        return caretakerVerificationRepository.save(v)
                .doOnSuccess(saved -> log.info("Recorded verification for caretakerId={}, status={}", caretakerId, saved.getVerificationStatus()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_ADMIN')")
    public Flux<CaretakerVerification> getCaretakerVerifications(Long caretakerId) {
        return caretakerVerificationRepository != null ? caretakerVerificationRepository.findByCaretakerIdOrderByVerifiedAtDesc(caretakerId) : Flux.empty();
    }
}
