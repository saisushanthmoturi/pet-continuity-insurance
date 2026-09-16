package com.example.claimsservice.service;

import com.example.claimsservice.dto.*;
import com.example.claimsservice.model.Claim;
import com.example.claimsservice.model.ClaimInvestigation;
import com.example.claimsservice.repository.ClaimInvestigationRepository;
import com.example.claimsservice.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClaimsServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimInvestigationRepository investigationRepository;

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

    private Claim sampleClaim;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        claimsService = new ClaimsService(claimRepository, investigationRepository, webClientBuilder, circuitBreakerFactory);

        sampleClaim = new Claim();
        sampleClaim.setClaimId(10L);
        sampleClaim.setPolicyId(100L);
        sampleClaim.setClaimantName("Jane Doe");
        sampleClaim.setRelationship("FAMILY");
        sampleClaim.setDeathCertificateNo("DC-987654321");
        sampleClaim.setStatus("PENDING");
        sampleClaim.setSubmittedAt(LocalDateTime.now());
        sampleClaim.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @SuppressWarnings("unchecked")
    void fileClaim_successful() {
        ClaimRequest req = new ClaimRequest(100L, "Jane Doe", "FAMILY", "DC-987654321", "2024-02-01", "Notes");
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
    void investigateClaim_invalidClaimStatus_throwsError() {
        sampleClaim.setStatus("APPROVED");
        when(circuitBreakerFactory.create("claimsCB")).thenReturn(circuitBreaker);
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimsService.investigateClaim(10L))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("cannot run investigation"))
                .verify();
    }

    @Test
    void getById_found() {
        when(claimRepository.findById(10L)).thenReturn(Mono.just(sampleClaim));
        when(investigationRepository.findByClaimId(10L)).thenReturn(Mono.empty());

        StepVerifier.create(claimsService.getById(10L))
                .assertNext(res -> assertEquals(10L, res.id()))
                .verifyComplete();
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
}
