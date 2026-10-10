package com.example.claimsservice.service;

import com.example.claimsservice.dto.*;
import com.example.claimsservice.model.Claim;
import com.example.claimsservice.model.ClaimDocument;
import com.example.claimsservice.model.ClaimInvestigation;
import com.example.claimsservice.model.ClaimStatusHistory;
import com.example.claimsservice.repository.ClaimDocumentRepository;
import com.example.claimsservice.repository.ClaimInvestigationRepository;
import com.example.claimsservice.repository.ClaimRepository;
import com.example.claimsservice.repository.ClaimStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClaimsServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimInvestigationRepository investigationRepository;

    @Mock
    private ClaimDocumentRepository documentRepository;

    @Mock
    private ClaimStatusHistoryRepository historyRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec bodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Mock
    private ReactiveCircuitBreaker circuitBreaker;

    private ClaimsService claimsService;
    private ClaimsService claimsServiceWithRepos;

    private Claim sampleClaim;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        claimsService = new ClaimsService(claimRepository, investigationRepository, webClientBuilder, circuitBreakerFactory);
        claimsServiceWithRepos = new ClaimsService(claimRepository, investigationRepository, documentRepository, historyRepository, webClientBuilder, circuitBreakerFactory);

        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));
        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> inv.getArgument(0));

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.empty());

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.header(anyString(), anyString())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.bodyValue(any())).thenReturn(uriSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        sampleClaim = new Claim();
        sampleClaim.setClaimId(10L);
        sampleClaim.setPolicyId(100L);
        sampleClaim.setClaimantName("Jane Doe");
        sampleClaim.setRelationship("FAMILY");
        sampleClaim.setDeathCertificateNo("DC-987654321");
        sampleClaim.setStatus("PENDING");
        sampleClaim.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @SuppressWarnings("unchecked")
    void fileClaim_successful() {
        ClaimRequest req = new ClaimRequest(100L, "Jane Doe", "FAMILY", "DC-987654321", "2024-01-01", "Notes");
        PolicyDto policyDto = new PolicyDto(100L, "POL-100", 5L, 10L, 20L, 10000.0, 50.0, "ACTIVE");

        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PolicyDto.class)).thenReturn(Mono.just(policyDto));

        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimsService.fileClaim(req))
                .assertNext(res -> {
                    assertEquals(10L, res.id());
                    assertEquals(100L, res.policyId());
                })
                .verifyComplete();

        verify(claimRepository).save(any(Claim.class));
    }

    @Test
    void fileClaim_missingFields_throwsError() {
        ClaimRequest reqNoPolicy = new ClaimRequest(null, "Jane", "FAMILY", "DC-123", "2024-01-01", null);
        StepVerifier.create(claimsService.fileClaim(reqNoPolicy))
                .expectError(IllegalArgumentException.class)
                .verify();

        ClaimRequest reqNoClaimant = new ClaimRequest(100L, null, "FAMILY", "DC-123", "2024-01-01", null);
        StepVerifier.create(claimsService.fileClaim(reqNoClaimant))
                .expectError(IllegalArgumentException.class)
                .verify();

        ClaimRequest reqNoCert = new ClaimRequest(100L, "Jane", "FAMILY", null, "2024-01-01", null);
        StepVerifier.create(claimsService.fileClaim(reqNoCert))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    void fileClaim_cbFallback_throwsIllegalStateException() {
        ClaimRequest req = new ClaimRequest(100L, "Jane", "FAMILY", "DC-123", "2024-01-01", null);

        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> {
            Function<Throwable, Mono<?>> fallback = inv.getArgument(1);
            return fallback.apply(new RuntimeException("CB error"));
        });

        StepVerifier.create(claimsService.fileClaim(req))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("PolicyService unavailable"))
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    void fileClaim_policyEmpty_throwsIllegalArgumentException() {
        ClaimRequest req = new ClaimRequest(100L, "Jane", "FAMILY", "DC-123", "2024-01-01", null);

        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(Mono.class), any())).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.fileClaim(req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Policy not found"))
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    void investigateClaim_shortCert_triggersManualReview() {
        sampleClaim.setStatus("VERIFIED");
        sampleClaim.setDeathCertificateNo("123"); // short length < 5
        PolicyDto activePolicy = new PolicyDto(100L, "POL-100", 5L, 10L, 20L, 10000.0, 50.0, "ACTIVE");

        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(responseSpec.bodyToMono(PolicyDto.class)).thenReturn(Mono.just(activePolicy));
        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

        ClaimInvestigation inv = ClaimInvestigation.create(10L, true, true, 65, "MANUAL_REVIEW", "High fraud indicator");
        when(investigationRepository.save(any(ClaimInvestigation.class))).thenReturn(Mono.just(inv));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimsService.investigateClaim(10L))
                .assertNext(res -> {
                    assertEquals("MANUAL_REVIEW", sampleClaim.getStatus());
                    assertEquals("MANUAL_REVIEW", res.investigationDecision());
                })
                .verifyComplete();
    }

    @Test
    void verifyDeath_verifiedTrue_setsStatusVerified() {
        DeathVerificationRequest req = new DeathVerificationRequest(true, "Registry confirmed");
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.verifyDeath(10L, req))
                .assertNext(res -> assertEquals("VERIFIED", sampleClaim.getStatus()))
                .verifyComplete();
    }

    @Test
    void verifyDeath_verifiedFalse_setsStatusRejected() {
        DeathVerificationRequest req = new DeathVerificationRequest(false, "Registry could not find certificate");
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.verifyDeath(10L, req))
                .assertNext(res -> {
                    assertEquals("REJECTED", sampleClaim.getStatus());
                    assertEquals("Registry could not find certificate", sampleClaim.getRejectionReason());
                })
                .verifyComplete();
    }

    @Test
    void verifyDeath_notFound_throwsIllegalArgumentException() {
        DeathVerificationRequest req = new DeathVerificationRequest(true, "OK");
        when(claimRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.verifyDeath(99L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    void investigateClaim_activePolicyAndValidCert_approved() {
        sampleClaim.setStatus("VERIFIED");
        PolicyDto activePolicy = new PolicyDto(100L, "POL-100", 5L, 10L, 20L, 10000.0, 50.0, "ACTIVE");

        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PolicyDto.class)).thenReturn(Mono.just(activePolicy));

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.header(anyString(), anyString())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.bodyValue(any())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

        ClaimInvestigation inv = ClaimInvestigation.create(10L, true, true, 10, "APPROVED", "Approved notes");
        when(investigationRepository.save(any(ClaimInvestigation.class))).thenReturn(Mono.just(inv));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimsService.investigateClaim(10L))
                .assertNext(res -> {
                    assertEquals("APPROVED", sampleClaim.getStatus());
                    assertEquals("APPROVED", res.investigationDecision());
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void investigateClaim_inactivePolicy_rejected() {
        sampleClaim.setStatus("VERIFIED");
        PolicyDto inactivePolicy = new PolicyDto(100L, "POL-100", 5L, 10L, 20L, 10000.0, 50.0, "TERMINATED");

        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PolicyDto.class)).thenReturn(Mono.just(inactivePolicy));

        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

        ClaimInvestigation inv = ClaimInvestigation.create(10L, false, true, 10, "REJECTED", "Policy not active");
        when(investigationRepository.save(any(ClaimInvestigation.class))).thenReturn(Mono.just(inv));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimsService.investigateClaim(10L))
                .assertNext(res -> {
                    assertEquals("REJECTED", sampleClaim.getStatus());
                    assertEquals("REJECTED", res.investigationDecision());
                })
                .verifyComplete();
    }

    @Test
    void investigateClaim_notFound_throwsIllegalArgumentException() {
        when(claimRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.investigateClaim(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void investigateClaim_invalidClaimStatus_throwsError() {
        sampleClaim.setStatus("APPROVED");
        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimsService.investigateClaim(10L))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("cannot run investigation"))
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    void approveClaim_alreadyFinalized_throwsIllegalStateException() {
        sampleClaim.setStatus("APPROVED");
        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimsService.approveClaim(10L))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("cannot run investigation"))
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    void approveClaim_fallbacksExecuted() {
        sampleClaim.setStatus("PENDING");
        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));

        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> {
            Function<Throwable, Mono<?>> fallback = inv.getArgument(1);
            return fallback.apply(new RuntimeException("Fallback error"));
        });

        ClaimInvestigation inv = ClaimInvestigation.create(10L, true, true, 0, "APPROVED", "Manual approval");
        when(investigationRepository.save(any(ClaimInvestigation.class))).thenReturn(Mono.just(inv));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimsService.approveClaim(10L))
                .assertNext(res -> {
                    assertEquals("APPROVED", sampleClaim.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void rejectClaim_withReason() {
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.rejectClaim(10L, "Fraudulent documentation"))
                .assertNext(res -> {
                    assertEquals("REJECTED", sampleClaim.getStatus());
                    assertEquals("Fraudulent documentation", sampleClaim.getRejectionReason());
                })
                .verifyComplete();
    }

    @Test
    void rejectClaim_withBlankReason() {
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.rejectClaim(10L, "  "))
                .assertNext(res -> {
                    assertEquals("REJECTED", sampleClaim.getStatus());
                    assertEquals("Claim rejected by claims officer", sampleClaim.getRejectionReason());
                })
                .verifyComplete();
    }

    @Test
    void rejectClaim_notFound() {
        when(claimRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.rejectClaim(99L, "Reason"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getAll_returnsFlux() {
        when(claimRepository.findAll()).thenReturn(Flux.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.getAll())
                .assertNext(res -> assertEquals(10L, res.id()))
                .verifyComplete();
    }

    @Test
    void updateClaim_success() {
        ClaimRequest updateReq = new ClaimRequest(100L, "New Name", "BROTHER", "DC-NEW", "2024-02-02", "New Notes");
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(Mono.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.updateClaim(10L, updateReq))
                .assertNext(res -> {
                    assertEquals("New Name", sampleClaim.getClaimantName());
                    assertEquals("BROTHER", sampleClaim.getRelationship());
                    assertEquals("DC-NEW", sampleClaim.getDeathCertificateNo());
                    assertEquals("2024-02-02", sampleClaim.getDateOfDeath());
                    assertEquals("New Notes", sampleClaim.getNotes());
                })
                .verifyComplete();
    }

    @Test
    void updateClaim_notFound() {
        ClaimRequest updateReq = new ClaimRequest(100L, "Name", "REL", "DC-1", "2024", null);
        when(claimRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.updateClaim(99L, updateReq))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getById_foundWithInvestigation() {
        ClaimInvestigation inv = ClaimInvestigation.create(10L, true, true, 10, "APPROVED", "Notes");
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.just(inv));

        StepVerifier.create(claimsService.getById(10L))
                .assertNext(res -> {
                    assertEquals(10L, res.id());
                    assertEquals("APPROVED", res.investigationDecision());
                    assertEquals(10, res.fraudScore());
                })
                .verifyComplete();
    }

    @Test
    void getById_notFound() {
        when(claimRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.getById(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getByPolicyId_returnsFlux() {
        when(claimRepository.findByPolicyId(100L)).thenReturn(Flux.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.getByPolicyId(100L))
                .expectNextMatches(res -> res.id().equals(10L))
                .verifyComplete();
    }

    @Test
    void deleteClaim_successful() {
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(claimRepository.delete(sampleClaim)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.deleteClaim(10L))
                .verifyComplete();

        verify(claimRepository).delete(sampleClaim);
    }

    @Test
    void deleteClaim_notFound() {
        when(claimRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.deleteClaim(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void toResponse_legacyReasonFallback() {
        Claim legacyClaim = new Claim();
        legacyClaim.setClaimId(20L);
        legacyClaim.setPolicyId(200L);
        legacyClaim.setClaimReason("Continuity care claim filed by Mark (BROTHER); Cert: DC-55555");

        when(claimRepository.findById(20L)).thenReturn(Mono.just(legacyClaim));
        when(investigationRepository.findByClaimId(20L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.getById(20L))
                .assertNext(res -> {
                    assertEquals("Mark", res.claimantName());
                    assertEquals("BROTHER", res.relationship());
                    assertEquals("DC-55555", res.deathCertificateNo());
                })
                .verifyComplete();
    }

    @Test
    void addDocument_withoutRepos_returnsEmpty() {
        DocumentRequest req = new DocumentRequest("DEATH_CERT", "cert.pdf", "/ref", "VERIFIED");
        StepVerifier.create(claimsService.addDocument(10L, req))
                .verifyComplete();
    }

    @Test
    void addDocument_withRepos_success() {
        DocumentRequest req = new DocumentRequest("DEATH_CERT", "cert.pdf", "/ref", "VERIFIED");
        ClaimDocument doc = new ClaimDocument(1L, 10L, "DEATH_CERT", "cert.pdf", "/ref", "VERIFIED", LocalDateTime.now());

        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(documentRepository.save(any(ClaimDocument.class))).thenReturn(Mono.just(doc));

        StepVerifier.create(claimsServiceWithRepos.addDocument(10L, req))
                .assertNext(d -> {
                    assertEquals(1L, d.getId());
                    assertEquals(10L, d.getClaimId());
                })
                .verifyComplete();
    }

    @Test
    void addDocument_withRepos_defaultValues() {
        DocumentRequest req = new DocumentRequest(null, null, null, null);
        ClaimDocument doc = new ClaimDocument(1L, 10L, "DEATH_CERTIFICATE", "document.pdf", "/docs/10", "VERIFIED", LocalDateTime.now());

        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(documentRepository.save(any(ClaimDocument.class))).thenReturn(Mono.just(doc));

        StepVerifier.create(claimsServiceWithRepos.addDocument(10L, req))
                .assertNext(d -> assertEquals(1L, d.getId()))
                .verifyComplete();
    }

    @Test
    void addDocument_withRepos_claimNotFound() {
        DocumentRequest req = new DocumentRequest("CERT", "file.pdf", "/ref", "VERIFIED");
        when(claimRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsServiceWithRepos.addDocument(99L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getDocumentsByClaimId_tests() {
        // null repo
        StepVerifier.create(claimsService.getDocumentsByClaimId(10L))
                .verifyComplete();

        // with repo
        ClaimDocument doc = new ClaimDocument(1L, 10L, "TYPE", "file.pdf", "/ref", "OK", LocalDateTime.now());
        when(documentRepository.findByClaimId(10L)).thenReturn(Flux.just(doc));

        StepVerifier.create(claimsServiceWithRepos.getDocumentsByClaimId(10L))
                .assertNext(d -> assertEquals(1L, d.getId()))
                .verifyComplete();
    }

    @Test
    void getClaimStatusHistory_tests() {
        // null repo
        StepVerifier.create(claimsService.getClaimStatusHistory(10L))
                .verifyComplete();

        // with repo
        ClaimStatusHistory h = ClaimStatusHistory.create(10L, "PENDING", "APPROVED", "Reason");
        when(historyRepository.findByClaimIdOrderByChangedAtDesc(10L)).thenReturn(Flux.just(h));

        StepVerifier.create(claimsServiceWithRepos.getClaimStatusHistory(10L))
                .assertNext(hist -> assertEquals(10L, hist.getClaimId()))
                .verifyComplete();
    }
}
