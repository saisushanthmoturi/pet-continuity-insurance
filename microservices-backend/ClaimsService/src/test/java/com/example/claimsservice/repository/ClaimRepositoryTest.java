package com.example.claimsservice.repository;

import com.example.claimsservice.model.Claim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimRepositoryTest {

    @Mock
    private ClaimRepository claimRepository;

    private Claim sampleClaim;

    @BeforeEach
    void setUp() {
        sampleClaim = Claim.create(2L, "Jane Doe", "SPOUSE", "DC-998877", "2026-09-20", "Notes");
        sampleClaim.setId(1L);
    }

    @Test
    void findByPolicyId_Found() {
        when(claimRepository.findByPolicyId(2L)).thenReturn(Flux.just(sampleClaim));

        StepVerifier.create(claimRepository.findByPolicyId(2L))
                .expectNextMatches(c -> c.getPolicyId().equals(2L) && c.getClaimNumber() != null)
                .verifyComplete();
    }

    @Test
    void findByClaimNumber_Found() {
        when(claimRepository.findByClaimNumber(sampleClaim.getClaimNumber())).thenReturn(Mono.just(sampleClaim));

        StepVerifier.create(claimRepository.findByClaimNumber(sampleClaim.getClaimNumber()))
                .expectNextMatches(c -> c.getPolicyId().equals(2L))
                .verifyComplete();
    }
}
