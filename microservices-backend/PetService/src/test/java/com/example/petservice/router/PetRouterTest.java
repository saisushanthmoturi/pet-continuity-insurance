package com.example.petservice.router;

import com.example.petservice.dto.PetRequest;
import com.example.petservice.handler.PetHandler;
import com.example.petservice.model.Pet;
import com.example.petservice.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetRouterTest {

    @Mock
    private PetService petService;

    private WebTestClient webTestClient;
    private Pet samplePet;

    @BeforeEach
    void setUp() {
        PetHandler handler = new PetHandler(petService);
        PetRouter router = new PetRouter();
        RouterFunction<ServerResponse> routes = router.petRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        samplePet = Pet.createNew(1L, "Buddy", "DOG", "Golden Retriever", 4, 31.5, "MALE", 1200.0);
        samplePet.setId(1L);
    }

    @Test
    void createPet_Success() {
        when(petService.createPet(any(PetRequest.class))).thenReturn(Mono.just(samplePet));

        PetRequest request = new PetRequest(1L, "Buddy", "DOG", "Golden Retriever", 4, 31.5, "MALE", 1200.0);

        webTestClient.post()
                .uri("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Buddy")
                .jsonPath("$.breed").isEqualTo("Golden Retriever");
    }

    @Test
    void getPetById_Found() {
        when(petService.getPetById(1L)).thenReturn(Mono.just(samplePet));

        webTestClient.get()
                .uri("/api/pets/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Buddy");
    }

    @Test
    void getPetById_NotFound() {
        when(petService.getPetById(99L)).thenReturn(Mono.error(new IllegalArgumentException("Pet not found with id: 99")));

        webTestClient.get()
                .uri("/api/pets/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getPetsByCustomerId_ReturnsList() {
        when(petService.getPetsByCustomerId(1L)).thenReturn(Flux.just(samplePet));

        webTestClient.get()
                .uri("/api/pets/customer/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Buddy");
    }
}
