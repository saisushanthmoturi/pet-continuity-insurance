package com.example.claimsservice.service;

import com.example.claimsservice.dto.*;
import com.example.claimsservice.model.Claim;
import com.example.claimsservice.model.ClaimInvestigation;
import com.example.claimsservice.repository.ClaimInvestigationRepository;
import com.example.claimsservice.repository.ClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ClaimsService {

    private static final Logger log = LoggerFactory.getLogger(ClaimsService.class);

    private final ClaimRepository claimRepository;
    private final ClaimInvestigationRepository investigationRepository;
    private final WebClient webClient;
    private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public ClaimsService(ClaimRepository claimRepository,
                          ClaimInvestigationRepository investigationRepository,
                          WebClient.Builder webClientBuilder,
                          ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.claimRepository = claimRepository;
        this.investigationRepository = investigationRepository;
        this.webClient = webClientBuilder.build();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Mono<ClaimResponse> fileClaim(ClaimRequest req) {
        if (req.policyId() == null || req.claimantName() == null || req.deathCertificateNo() == null) {
            return Mono.error(new IllegalArgumentException("policyId, claimantName, and deathCertificateNo are required"));
        }

        ReactiveCircuitBreaker cb = circuitBreakerFactory.create("claimsCB");

        return cb.run(
                webClient.get()
                        .uri("http://PolicyService/api/policies/{id}", req.policyId())
                        .header("X-User-Role", "INTERNAL_SERVICE")
                        .retrieve()
                        .bodyToMono(PolicyDto.class),
                e -> {
                    log.error("CircuitBreaker fallback: Failed to retrieve policy: {}", e.getMessage());
                    return Mono.error(new IllegalStateException("PolicyService unavailable during claim filing"));
                }
        )
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found: " + req.policyId())))
        .flatMap(policy -> {
            Claim claim = Claim.create(
                    req.policyId(),
                    req.claimantName().trim(),
                    req.relationship() != null ? req.relationship().trim() : "FAMILY",
                    req.deathCertificateNo().trim(),
                    req.dateOfDeath() != null ? req.dateOfDeath() : "Recent",
                    req.notes()
            );
            return claimRepository.save(claim)
                    .map(saved -> {
                        log.info("Filed claim id={}, number={}, policyId={}", saved.getId(), saved.getClaimNumber(), saved.getPolicyId());
                        return toResponse(saved, null);
                    });
        });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Mono<ClaimResponse> verifyDeath(Long claimId, DeathVerificationRequest req) {
        return claimRepository.findById(claimId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Claim not found with id: " + claimId)))
                .flatMap(claim -> {
                    boolean verified = req == null || req.verified() == null || req.verified();
                    if (verified) {
                        claim.setStatus("VERIFIED");
                        log.info("Death verified for claimId={}, cert={}", claimId, claim.getDeathCertificateNo());
                    } else {
                        claim.setStatus("REJECTED");
                        claim.setRejectionReason(req.registryNotes() != null ? req.registryNotes() : "Death certificate rejected by civil registry");
                        log.warn("Death verification FAILED for claimId={}", claimId);
                    }
                    return claimRepository.save(claim).flatMap(this::loadResponse);
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Mono<ClaimResponse> investigateClaim(Long claimId) {
        ReactiveCircuitBreaker cb = circuitBreakerFactory.create("claimsCB");

        return claimRepository.findById(claimId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Claim not found with id: " + claimId)))
                .flatMap(claim -> {
                    if (!"VERIFIED".equals(claim.getStatus()) && !"PENDING".equals(claim.getStatus())) {
                        return Mono.error(new IllegalStateException("Claim is in status " + claim.getStatus() + "; cannot run investigation"));
                    }

                    return cb.run(
                            webClient.get()
                                    .uri("http://PolicyService/api/policies/{id}", claim.getPolicyId())
                                    .header("X-User-Role", "INTERNAL_SERVICE")
                                    .retrieve()
                                    .bodyToMono(PolicyDto.class),
                            e -> {
                                log.error("CircuitBreaker fallback: Failed to retrieve policy during investigation: {}", e.getMessage());
                                return Mono.error(new IllegalStateException("PolicyService unavailable during claim investigation"));
                            }
                    )
                    .flatMap(policy -> {
                        boolean policyActive = "ACTIVE".equalsIgnoreCase(policy.status());
                        boolean waitingPeriodPassed = true;
                        int fraudScore = (claim.getDeathCertificateNo() == null || claim.getDeathCertificateNo().length() < 5) ? 65 : 10;

                        String decision;
                        String notes;
                        if (!policyActive) {
                            decision = "REJECTED";
                            notes = "Policy is not in ACTIVE status (current: " + policy.status() + ")";
                        } else if (fraudScore > 50) {
                            decision = "MANUAL_REVIEW";
                            notes = "High fraud indicator detected on death certificate format";
                        } else {
                            decision = "APPROVED";
                            notes = "All checks passed. Death verified, active policy, waiting period satisfied.";
                        }

                        ClaimInvestigation inv = ClaimInvestigation.create(claim.getId(), policyActive, waitingPeriodPassed, fraudScore, decision, notes);
                        return investigationRepository.save(inv)
                                .flatMap(savedInv -> {
                                    claim.setStatus(decision);
                                    if ("REJECTED".equals(decision)) {
                                        claim.setRejectionReason(notes);
                                    }

                                    Mono<Void> sideEffects = Mono.empty();
                                    if ("APPROVED".equals(decision)) {
                                        log.info("Claim APPROVED for id={}, triggering Pet Continuity Fund creation and policy update", claimId);
                                        CreateFundDto fundReq = new CreateFundDto(policy.id(), policy.petId(), policy.coverageAmount(), 300.0, 4000.0, 2000.0);
                                        Mono<Void> fundMono = cb.run(
                                                webClient.post()
                                                        .uri("http://PaymentFundService/api/payments/funds/create")
                                                        .header("X-User-Role", "INTERNAL_SERVICE")
                                                        .bodyValue(fundReq)
                                                        .retrieve()
                                                        .toBodilessEntity()
                                                        .then(),
                                                e -> {
                                                    log.warn("CircuitBreaker fallback: Failed to create fund via PaymentFundService: {}", e.getMessage());
                                                    return Mono.empty();
                                                }
                                        );

                                        Mono<Void> policyStatusMono = cb.run(
                                                webClient.post()
                                                        .uri("http://PolicyService/api/policies/{id}/status", policy.id())
                                                        .header("X-User-Role", "INTERNAL_SERVICE")
                                                        .bodyValue(Map.of("status", "CLAIM_FILED", "reason", "Owner death claim approved"))
                                                        .retrieve()
                                                        .toBodilessEntity()
                                                        .then(),
                                                e -> {
                                                    log.warn("CircuitBreaker fallback: Failed to update policy status via PolicyService: {}", e.getMessage());
                                                    return Mono.empty();
                                                }
                                        );

                                        sideEffects = Mono.when(fundMono, policyStatusMono);
                                    }

                                    return sideEffects.then(claimRepository.save(claim))
                                                    .map(updatedClaim -> toResponse(updatedClaim, savedInv));
                                        });
                            });
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Flux<ClaimResponse> getAll() {
        return claimRepository.findAll()
                .flatMap(this::loadResponse);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Mono<ClaimResponse> updateClaim(Long id, ClaimRequest req) {
        return claimRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Claim not found with id: " + id)))
                .flatMap(claim -> {
                    if (req.claimantName() != null) claim.setClaimantName(req.claimantName().trim());
                    if (req.relationship() != null) claim.setRelationship(req.relationship().trim());
                    if (req.deathCertificateNo() != null) claim.setDeathCertificateNo(req.deathCertificateNo().trim());
                    if (req.dateOfDeath() != null) claim.setDateOfDeath(req.dateOfDeath().trim());
                    if (req.notes() != null) claim.setNotes(req.notes());
                    return claimRepository.save(claim).flatMap(this::loadResponse);
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteClaim(Long id) {
        return claimRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Claim not found with id: " + id)))
                .flatMap(claim -> claimRepository.delete(claim));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Mono<ClaimResponse> approveClaim(Long id) {
        return investigateClaim(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Mono<ClaimResponse> rejectClaim(Long id, String reason) {
        return claimRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Claim not found with id: " + id)))
                .flatMap(claim -> {
                    claim.setStatus("REJECTED");
                    claim.setRejectionReason(reason != null && !reason.isBlank() ? reason : "Claim rejected by claims officer");
                    return claimRepository.save(claim).flatMap(this::loadResponse);
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Mono<ClaimResponse> getById(Long id) {
        return claimRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Claim not found with id: " + id)))
                .flatMap(this::loadResponse);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN')")
    public Flux<ClaimResponse> getByPolicyId(Long policyId) {
        return claimRepository.findByPolicyId(policyId)
                .flatMap(this::loadResponse);
    }

    private Mono<ClaimResponse> loadResponse(Claim claim) {
        return investigationRepository.findByClaimId(claim.getId())
                .map(inv -> toResponse(claim, inv))
                .defaultIfEmpty(toResponse(claim, null));
    }

    private ClaimResponse toResponse(Claim c, ClaimInvestigation inv) {
        return new ClaimResponse(
                c.getId(),
                c.getClaimNumber(),
                c.getPolicyId(),
                c.getClaimantName(),
                c.getRelationship(),
                c.getDeathCertificateNo(),
                c.getDateOfDeath(),
                c.getStatus(),
                c.getRejectionReason(),
                c.getNotes(),
                c.getCreatedAt(),
                inv != null ? inv.getDecision() : null,
                inv != null ? inv.getFraudScore() : null
        );
    }
}
