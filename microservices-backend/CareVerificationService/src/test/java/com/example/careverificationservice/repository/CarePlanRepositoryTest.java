package com.example.careverificationservice.repository;

import com.example.careverificationservice.model.CarePlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarePlanRepositoryTest {

    @Mock
    private CarePlanRepository carePlanRepository;

    private CarePlan sampleCarePlan;

    @BeforeEach
    void setUp() {
        sampleCarePlan = new CarePlan();
        sampleCarePlan.setId(1L);
        sampleCarePlan.setPetId(10L);
        sampleCarePlan.setPrimaryCaretakerId(1L);
        sampleCarePlan.setBackupCaretakerId(2L);
    }

    @Test
    void findByPetId_Found() {
        when(carePlanRepository.findByPetId(10L)).thenReturn(Mono.just(sampleCarePlan));

        StepVerifier.create(carePlanRepository.findByPetId(10L))
                .expectNextMatches(plan -> plan.getPetId().equals(10L) && plan.getPrimaryCaretakerId().equals(1L))
                .verifyComplete();
    }

    @Test
    void findByPetId_Empty() {
        when(carePlanRepository.findByPetId(99L)).thenReturn(Mono.empty());

        StepVerifier.create(carePlanRepository.findByPetId(99L))
                .verifyComplete();
    }
}
