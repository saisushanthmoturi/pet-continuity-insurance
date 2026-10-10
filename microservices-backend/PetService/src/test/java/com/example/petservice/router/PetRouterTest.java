package com.example.petservice.router;

import com.example.petservice.dto.MedicalRecordRequest;
import com.example.petservice.dto.PetRequest;
import com.example.petservice.handler.PetHandler;
import com.example.petservice.model.Pet;
import com.example.petservice.model.PetMedicalRecord;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetRouterTest {

    @Mock
    private PetService petService;

    private WebTestClient webTestClient;
    private Pet samplePet;
    private PetMedicalRecord sampleRecord;

    @BeforeEach
    void setUp() {
        PetHandler handler = new PetHandler(petService);
        PetRouter router = new PetRouter();
        RouterFunction<ServerResponse> routes = router.petRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        samplePet = Pet.createNew(1L, "Buddy", "DOG", "Golden Retriever", 4, 31.5, "MALE", 1200.0);
        samplePet.setId(1L);

        sampleRecord = PetMedicalRecord.createNew(1L, "Allergy", "2024-01-01", "Treatment", 200.0);
        sampleRecord.setId(10L);
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
    void getAllPets_ReturnsList() {
        when(petService.getAllPets()).thenReturn(Flux.just(samplePet));

        webTestClient.get()
                .uri("/api/pets")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Pet.class).hasSize(1);
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
                .expectBodyList(Pet.class).hasSize(1);
    }

    @Test
    void updatePet_Success() {
        when(petService.updatePet(eq(1L), any(PetRequest.class))).thenReturn(Mono.just(samplePet));

        webTestClient.put()
                .uri("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PetRequest(1L, "Buddy", "DOG", "Golden Retriever", 4, 31.5, "MALE", 1200.0))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void deletePet_Success() {
        when(petService.deletePet(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/pets/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void addMedicalRecord_Success() {
        when(petService.addMedicalRecord(eq(1L), any(MedicalRecordRequest.class))).thenReturn(Mono.just(sampleRecord));

        webTestClient.post()
                .uri("/api/pets/1/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MedicalRecordRequest("Allergy", "2024-01-01", "Treatment", 200.0))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10);
    }

    @Test
    void getMedicalRecordsByPetId_ReturnsList() {
        when(petService.getMedicalRecordsByPetId(1L)).thenReturn(Flux.just(sampleRecord));

        webTestClient.get()
                .uri("/api/pets/1/medical-records")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PetMedicalRecord.class).hasSize(1);
    }

    @Test
    void getMedicalRecordById_Found() {
        when(petService.getMedicalRecordById(10L)).thenReturn(Mono.just(sampleRecord));

        webTestClient.get()
                .uri("/api/pets/medical-records/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10);
    }

    @Test
    void updateMedicalRecord_Success() {
        when(petService.updateMedicalRecord(eq(10L), any(MedicalRecordRequest.class))).thenReturn(Mono.just(sampleRecord));

        webTestClient.put()
                .uri("/api/pets/medical-records/10")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MedicalRecordRequest("Allergy", "2024-01-01", "Treatment", 200.0))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10);
    }

    @Test
    void deleteMedicalRecord_Success() {
        when(petService.deleteMedicalRecord(10L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/pets/medical-records/10")
                .exchange()
                .expectStatus().isNoContent();
    }
}
