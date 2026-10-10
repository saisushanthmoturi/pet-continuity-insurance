package com.example.petservice.handler;

import com.example.petservice.dto.MedicalRecordRequest;
import com.example.petservice.dto.PetRequest;
import com.example.petservice.model.Pet;
import com.example.petservice.model.PetMedicalRecord;
import com.example.petservice.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetHandlerTest {

    @Mock
    private PetService petService;

    private PetHandler handler;
    private Pet samplePet;
    private PetMedicalRecord sampleRecord;

    @BeforeEach
    void setUp() {
        handler = new PetHandler(petService);
        samplePet = Pet.createNew(10L, "Buddy", "DOG", "Labrador", 3, 25.0, "MALE", 1200.0);
        samplePet.setId(1L);
        sampleRecord = PetMedicalRecord.createNew(1L, "Allergy", "2024-01-01", "Treatment", 200.0);
        sampleRecord.setId(10L);
    }

    @Test
    void createPet_success() {
        PetRequest req = new PetRequest(10L, "Buddy", "DOG", "Labrador", 3, 25.0, "MALE", 1200.0);
        when(petService.createPet(any(PetRequest.class))).thenReturn(Mono.just(samplePet));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        StepVerifier.create(handler.createPet(request))
                .assertNext(res -> assertEquals(HttpStatus.CREATED, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void createPet_error() {
        when(petService.createPet(any(PetRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid input")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new PetRequest(null, null, null, null, null, null, null, null)));
        StepVerifier.create(handler.createPet(request))
                .assertNext(res -> assertEquals(HttpStatus.BAD_REQUEST, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getPetById_success() {
        when(petService.getPetById(1L)).thenReturn(Mono.just(samplePet));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        StepVerifier.create(handler.getPetById(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getPetById_notFound() {
        when(petService.getPetById(99L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "99").build();
        StepVerifier.create(handler.getPetById(request))
                .assertNext(res -> assertEquals(HttpStatus.NOT_FOUND, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getPetsByCustomerId_success() {
        when(petService.getPetsByCustomerId(10L)).thenReturn(Flux.just(samplePet));

        MockServerRequest request = MockServerRequest.builder().pathVariable("customerId", "10").build();
        StepVerifier.create(handler.getPetsByCustomerId(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void addMedicalRecord_success() {
        MedicalRecordRequest req = new MedicalRecordRequest("Allergy", "2024-01-01", "Treatment", 200.0);
        when(petService.addMedicalRecord(eq(1L), any(MedicalRecordRequest.class))).thenReturn(Mono.just(sampleRecord));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(req));
        StepVerifier.create(handler.addMedicalRecord(request))
                .assertNext(res -> assertEquals(HttpStatus.CREATED, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void addMedicalRecord_error() {
        when(petService.addMedicalRecord(eq(1L), any(MedicalRecordRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(new MedicalRecordRequest(null, null, null, null)));
        StepVerifier.create(handler.addMedicalRecord(request))
                .assertNext(res -> assertEquals(HttpStatus.BAD_REQUEST, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getMedicalRecordsByPetId_success() {
        when(petService.getMedicalRecordsByPetId(1L)).thenReturn(Flux.just(sampleRecord));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        StepVerifier.create(handler.getMedicalRecordsByPetId(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllPets_success() {
        when(petService.getAllPets()).thenReturn(Flux.just(samplePet));

        MockServerRequest request = MockServerRequest.builder().build();
        StepVerifier.create(handler.getAllPets(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void updatePet_success() {
        PetRequest req = new PetRequest(10L, "Updated", "DOG", "Labrador", 4, 26.0, "MALE", 1300.0);
        when(petService.updatePet(eq(1L), any(PetRequest.class))).thenReturn(Mono.just(samplePet));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(req));
        StepVerifier.create(handler.updatePet(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void updatePet_error() {
        when(petService.updatePet(eq(1L), any(PetRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(new PetRequest(null, null, null, null, null, null, null, null)));
        StepVerifier.create(handler.updatePet(request))
                .assertNext(res -> assertEquals(HttpStatus.BAD_REQUEST, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void deletePet_success() {
        when(petService.deletePet(1L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        StepVerifier.create(handler.deletePet(request))
                .assertNext(res -> assertEquals(HttpStatus.NO_CONTENT, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void deletePet_notFound() {
        when(petService.deletePet(99L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "99").build();
        StepVerifier.create(handler.deletePet(request))
                .assertNext(res -> assertEquals(HttpStatus.NOT_FOUND, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteMedicalRecord_success() {
        when(petService.deleteMedicalRecord(10L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("recordId", "10").build();
        StepVerifier.create(handler.deleteMedicalRecord(request))
                .assertNext(res -> assertEquals(HttpStatus.NO_CONTENT, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteMedicalRecord_notFound() {
        when(petService.deleteMedicalRecord(99L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("recordId", "99").build();
        StepVerifier.create(handler.deleteMedicalRecord(request))
                .assertNext(res -> assertEquals(HttpStatus.NOT_FOUND, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getMedicalRecordById_success() {
        when(petService.getMedicalRecordById(10L)).thenReturn(Mono.just(sampleRecord));

        MockServerRequest request = MockServerRequest.builder().pathVariable("recordId", "10").build();
        StepVerifier.create(handler.getMedicalRecordById(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void getMedicalRecordById_notFound() {
        when(petService.getMedicalRecordById(99L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("recordId", "99").build();
        StepVerifier.create(handler.getMedicalRecordById(request))
                .assertNext(res -> assertEquals(HttpStatus.NOT_FOUND, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateMedicalRecord_success() {
        MedicalRecordRequest req = new MedicalRecordRequest("Allergy", "2024-01-01", "Treatment", 200.0);
        when(petService.updateMedicalRecord(eq(10L), any(MedicalRecordRequest.class))).thenReturn(Mono.just(sampleRecord));

        MockServerRequest request = MockServerRequest.builder().pathVariable("recordId", "10").body(Mono.just(req));
        StepVerifier.create(handler.updateMedicalRecord(request))
                .assertNext(res -> assertEquals(HttpStatus.OK, res.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateMedicalRecord_error() {
        when(petService.updateMedicalRecord(eq(10L), any(MedicalRecordRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("recordId", "10").body(Mono.just(new MedicalRecordRequest(null, null, null, null)));
        StepVerifier.create(handler.updateMedicalRecord(request))
                .assertNext(res -> assertEquals(HttpStatus.BAD_REQUEST, res.statusCode()))
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHandlerLambdasViaReflection() throws Exception {
        AccessDeniedException denied = new AccessDeniedException("Forbidden");
        IllegalArgumentException badArg = new IllegalArgumentException("Bad Argument");

        for (Method m : PetHandler.class.getDeclaredMethods()) {
            if (m.getName().startsWith("lambda$") && m.getParameterCount() == 1) {
                m.setAccessible(true);
                Class<?> paramType = m.getParameterTypes()[0];
                if (AccessDeniedException.class.isAssignableFrom(paramType)) {
                    Object res = m.invoke(null, denied);
                    if (res instanceof Mono<?> mono) {
                        StepVerifier.create((Mono<ServerResponse>) mono)
                                .assertNext(sr -> assertEquals(HttpStatus.FORBIDDEN, sr.statusCode()))
                                .verifyComplete();
                    }
                } else if (IllegalArgumentException.class.isAssignableFrom(paramType)) {
                    Object res = m.invoke(null, badArg);
                    if (res instanceof Mono<?> mono) {
                        StepVerifier.create((Mono<ServerResponse>) mono)
                                .expectNextCount(1)
                                .verifyComplete();
                    }
                }
            }
        }
    }
}
