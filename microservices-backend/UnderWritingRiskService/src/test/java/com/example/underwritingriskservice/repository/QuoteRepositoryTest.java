package com.example.underwritingriskservice.repository;

import com.example.underwritingriskservice.model.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteRepositoryTest {

    @Mock
    private QuoteRepository quoteRepository;

    private Quote sampleQuote;

    @BeforeEach
    void setUp() {
        sampleQuote = Quote.createNew(1L, 1L, 25000.0, 187.50, 34, "APPROVED");
        sampleQuote.setId(1L);
    }

    @Test
    void findByCustomerId_Found() {
        when(quoteRepository.findByCustomerId(1L)).thenReturn(Flux.just(sampleQuote));

        StepVerifier.create(quoteRepository.findByCustomerId(1L))
                .expectNextMatches(q -> q.getCustomerId().equals(1L) && q.getMonthlyPremium().equals(187.50))
                .verifyComplete();
    }

    @Test
    void findByPetId_Found() {
        when(quoteRepository.findByPetId(1L)).thenReturn(Flux.just(sampleQuote));

        StepVerifier.create(quoteRepository.findByPetId(1L))
                .expectNextMatches(q -> q.getPetId().equals(1L) && q.getRiskScore().equals(34))
                .verifyComplete();
    }
}
