package com.example.careverificationservice.router;

import com.example.careverificationservice.dto.CarePlanRequest;
import com.example.careverificationservice.dto.CaretakerRequest;
import com.example.careverificationservice.handler.CareHandler;
import com.example.careverificationservice.model.CarePlan;
import com.example.careverificationservice.model.Caretaker;
import com.example.careverificationservice.service.CareService;
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
class CareRouterTest {

    @Mock
    private CareService careService;

    private WebTestClient webTestClient;

    private Caretaker sampleCaretaker;
    private CarePlan sampleCarePlan;

    @BeforeEach
    void setUp() {
        CareHandler handler = new CareHandler(careService);
        CareRouter router = new CareRouter();
        RouterFunction<ServerResponse> routes = router.careRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        sampleCaretaker = Caretaker.create(1L, 1L, "Robert Smith", "+1-555-0211", "robert@example.com", "PRIMARY", "123 Elm St");
        sampleCaretaker.setId(1L);

        sampleCarePlan = new CarePlan();
        sampleCarePlan.setId(1L);
        sampleCarePlan.setPetId(1L);
        sampleCarePlan.setPrimaryCaretakerId(1L);
        sampleCarePlan.setBackupCaretakerId(2L);
    }

    @Test
    void addCaretaker_Success() {
        when(careService.addCaretaker(any(CaretakerRequest.class))).thenReturn(Mono.just(sampleCaretaker));

        CaretakerRequest request = new CaretakerRequest(1L, 1L, "Robert Smith", "+1-555-0211", "robert@example.com", "PRIMARY", "123 Elm St");

        webTestClient.post()
                .uri("/api/care/caretakers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.fullName").isEqualTo("Robert Smith");
    }

    @Test
    void getCaretakerById_Found() {
        when(careService.getCaretakerById(1L)).thenReturn(Mono.just(sampleCaretaker));

        webTestClient.get()
                .uri("/api/care/caretakers/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.fullName").isEqualTo("Robert Smith");
    }

    @Test
    void saveCarePlan_Success() {
        when(careService.saveCarePlan(any(CarePlanRequest.class))).thenReturn(Mono.just(sampleCarePlan));

        CarePlanRequest request = new CarePlanRequest(1L, 1L, 2L, "Springfield Vet", "2 cups kibble", "None");

        webTestClient.post()
                .uri("/api/care/care-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.petId").isEqualTo(1);
    }

    @Test
    void getCarePlanByPetId_Found() {
        when(careService.getCarePlanByPetId(1L)).thenReturn(Mono.just(sampleCarePlan));

        webTestClient.get()
                .uri("/api/care/care-plans/pet/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.petId").isEqualTo(1);
    }
}
