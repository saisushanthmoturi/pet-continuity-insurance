package com.example.petservice.repository;

import com.example.petservice.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetRepositoryTest {

    @Mock
    private PetRepository petRepository;

    private Pet samplePet;

    @BeforeEach
    void setUp() {
        samplePet = Pet.createNew(1L, "Buddy", "DOG", "Golden Retriever", 4, 31.5, "MALE", 1200.0);
        samplePet.setId(1L);
    }

    @Test
    void findByCustomerId_Found() {
        when(petRepository.findByCustomerId(1L)).thenReturn(Flux.just(samplePet));

        StepVerifier.create(petRepository.findByCustomerId(1L))
                .expectNextMatches(p -> p.getCustomerId().equals(1L) && p.getName().equals("Buddy"))
                .verifyComplete();
    }

    @Test
    void findByCustomerId_Empty() {
        when(petRepository.findByCustomerId(99L)).thenReturn(Flux.empty());

        StepVerifier.create(petRepository.findByCustomerId(99L))
                .verifyComplete();
    }
}
