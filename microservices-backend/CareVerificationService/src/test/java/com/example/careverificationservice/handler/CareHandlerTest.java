package com.example.careverificationservice.handler;

import com.example.careverificationservice.dto.CarePlanRequest;
import com.example.careverificationservice.dto.CaretakerRequest;
import com.example.careverificationservice.dto.EligibilityResponse;
import com.example.careverificationservice.dto.VerificationRequest;
import com.example.careverificationservice.model.*;
import com.example.careverificationservice.service.CareService;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareHandlerTest {

    @Mock
    private CareService careService;

    private CareHandler handler;
    private Caretaker sampleCaretaker;
    private CarePlan sampleCarePlan;
    private PetVerification sampleVerification;
    private CaretakerVerification sampleCaretakerVerification;
    private CareTransfer sampleTransfer;

    @BeforeEach
    void setUp() {
        handler = new CareHandler(careService);
        sampleCaretaker = Caretaker.create(10L, 20L, "John", "123", "john@example.com", "PRIMARY", "Addr");
        sampleCaretaker.setId(1L);

        sampleCarePlan = CarePlan.create(20L, 1L, 2L, "Vet", "Feed", "None");
        sampleCarePlan.setId(10L);

        sampleVerification = PetVerification.create(20L, 1L, "2026-10-10", "PASSED", "Healthy");
        sampleVerification.setId(100L);

        sampleCaretakerVerification = new CaretakerVerification(1L, 1L, "IDENTITY", "DOCUMENT", "VERIFIED", "REF", "Admin", null, null, null, null);
        sampleTransfer = new CareTransfer(1L, 20L, 1L, 2L, "Transfer", null, "Admin", "COMPLETED", null);
    }

    @Test
    void addCaretaker_success() {
        CaretakerRequest req = new CaretakerRequest(10L, 20L, "John", "123", "john@example.com", "PRIMARY", "Addr");
        when(careService.addCaretaker(any(CaretakerRequest.class))).thenReturn(Mono.just(sampleCaretaker));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        Mono<ServerResponse> response = handler.addCaretaker(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void addCaretaker_error_returnsBadRequest() {
        when(careService.addCaretaker(any(CaretakerRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new CaretakerRequest(null, null, null, null, null, null, null)));
        Mono<ServerResponse> response = handler.addCaretaker(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCaretakersByPetId_success() {
        when(careService.getCaretakersByPetId(20L)).thenReturn(Flux.just(sampleCaretaker));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.getCaretakersByPetId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateCaretakerStatus_success() {
        when(careService.updateCaretakerStatus(1L, "ACTIVE")).thenReturn(Mono.just(sampleCaretaker));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(Map.of("status", "ACTIVE")));
        Mono<ServerResponse> response = handler.updateCaretakerStatus(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateCaretakerStatus_error_returnsBadRequest() {
        when(careService.updateCaretakerStatus(1L, "ACTIVE")).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(Map.of("status", "ACTIVE")));
        Mono<ServerResponse> response = handler.updateCaretakerStatus(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void saveCarePlan_success() {
        CarePlanRequest req = new CarePlanRequest(20L, 1L, 2L, "Vet", "Feed", "None");
        when(careService.saveCarePlan(any(CarePlanRequest.class))).thenReturn(Mono.just(sampleCarePlan));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        Mono<ServerResponse> response = handler.saveCarePlan(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void saveCarePlan_error_returnsBadRequest() {
        when(careService.saveCarePlan(any(CarePlanRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new CarePlanRequest(null, null, null, null, null, null)));
        Mono<ServerResponse> response = handler.saveCarePlan(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCarePlanByPetId_found() {
        when(careService.getCarePlanByPetId(20L)).thenReturn(Mono.just(sampleCarePlan));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.getCarePlanByPetId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCarePlanByPetId_notFound() {
        when(careService.getCarePlanByPetId(20L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.getCarePlanByPetId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void recordVerification_success() {
        VerificationRequest req = new VerificationRequest(20L, 1L, "2026-10-10", "PASSED", "Healthy");
        when(careService.recordVerification(any(VerificationRequest.class))).thenReturn(Mono.just(sampleVerification));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        Mono<ServerResponse> response = handler.recordVerification(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void recordVerification_error_returnsBadRequest() {
        when(careService.recordVerification(any(VerificationRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new VerificationRequest(null, null, null, null, null)));
        Mono<ServerResponse> response = handler.recordVerification(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getVerificationsByPetId_success() {
        when(careService.getVerificationsByPetId(20L)).thenReturn(Flux.just(sampleVerification));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.getVerificationsByPetId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllCaretakers_success() {
        when(careService.getAllCaretakers()).thenReturn(Flux.just(sampleCaretaker));

        MockServerRequest request = MockServerRequest.builder().build();
        Mono<ServerResponse> response = handler.getAllCaretakers(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCaretakerById_found() {
        when(careService.getCaretakerById(1L)).thenReturn(Mono.just(sampleCaretaker));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getCaretakerById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCaretakerById_notFound() {
        when(careService.getCaretakerById(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getCaretakerById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateCaretaker_success() {
        CaretakerRequest req = new CaretakerRequest(10L, 20L, "John", "123", "john@example.com", "PRIMARY", "Addr");
        when(careService.updateCaretaker(eq(1L), any(CaretakerRequest.class))).thenReturn(Mono.just(sampleCaretaker));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(req));
        Mono<ServerResponse> response = handler.updateCaretaker(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateCaretaker_error_returnsBadRequest() {
        when(careService.updateCaretaker(eq(1L), any(CaretakerRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(new CaretakerRequest(null, null, null, null, null, null, null)));
        Mono<ServerResponse> response = handler.updateCaretaker(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteCaretaker_success() {
        when(careService.deleteCaretaker(1L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.deleteCaretaker(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteCaretaker_notFound() {
        when(careService.deleteCaretaker(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.deleteCaretaker(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void transferBackup_success() {
        when(careService.transferToBackup(20L)).thenReturn(Mono.just(sampleCaretaker));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.transferBackup(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void transferBackup_error_returnsBadRequest() {
        when(careService.transferToBackup(20L)).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.transferBackup(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllCarePlans_success() {
        when(careService.getAllCarePlans()).thenReturn(Flux.just(sampleCarePlan));

        MockServerRequest request = MockServerRequest.builder().build();
        Mono<ServerResponse> response = handler.getAllCarePlans(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCarePlanById_found() {
        when(careService.getCarePlanById(10L)).thenReturn(Mono.just(sampleCarePlan));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "10").build();
        Mono<ServerResponse> response = handler.getCarePlanById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCarePlanById_notFound() {
        when(careService.getCarePlanById(10L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "10").build();
        Mono<ServerResponse> response = handler.getCarePlanById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteCarePlan_success() {
        when(careService.deleteCarePlan(10L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "10").build();
        Mono<ServerResponse> response = handler.deleteCarePlan(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteCarePlan_notFound() {
        when(careService.deleteCarePlan(10L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "10").build();
        Mono<ServerResponse> response = handler.deleteCarePlan(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllVerifications_success() {
        when(careService.getAllVerifications()).thenReturn(Flux.just(sampleVerification));

        MockServerRequest request = MockServerRequest.builder().build();
        Mono<ServerResponse> response = handler.getAllVerifications(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getVerificationById_found() {
        when(careService.getVerificationById(100L)).thenReturn(Mono.just(sampleVerification));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();
        Mono<ServerResponse> response = handler.getVerificationById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getVerificationById_notFound() {
        when(careService.getVerificationById(100L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();
        Mono<ServerResponse> response = handler.getVerificationById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteVerification_success() {
        when(careService.deleteVerification(100L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();
        Mono<ServerResponse> response = handler.deleteVerification(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteVerification_notFound() {
        when(careService.deleteVerification(100L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();
        Mono<ServerResponse> response = handler.deleteVerification(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void checkEligibility_success() {
        EligibilityResponse resp = new EligibilityResponse(true, "Eligible", 1L);
        when(careService.checkMonthlyEligibility(20L, 1L)).thenReturn(Mono.just(resp));

        MockServerRequest request = MockServerRequest.builder()
                .queryParam("petId", "20")
                .queryParam("caretakerId", "1")
                .build();
        Mono<ServerResponse> response = handler.checkEligibility(request);

        StepVerifier.create(response)
                .assertNext(r -> assertEquals(HttpStatus.OK, r.statusCode()))
                .verifyComplete();
    }

    @Test
    void checkEligibility_error_returnsBadRequest() {
        when(careService.checkMonthlyEligibility(1L, 1L)).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().build();
        Mono<ServerResponse> response = handler.checkEligibility(request);

        StepVerifier.create(response)
                .assertNext(r -> assertEquals(HttpStatus.BAD_REQUEST, r.statusCode()))
                .verifyComplete();
    }

    @Test
    void getTransfers_success() {
        when(careService.getTransfersByPetId(20L)).thenReturn(Flux.just(sampleTransfer));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.getTransfers(request);

        StepVerifier.create(response)
                .assertNext(r -> assertEquals(HttpStatus.OK, r.statusCode()))
                .verifyComplete();
    }

    @Test
    void recordCaretakerVerification_success() {
        when(careService.recordCaretakerVerification(eq(1L), any(CaretakerVerification.class))).thenReturn(Mono.just(sampleCaretakerVerification));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(sampleCaretakerVerification));
        Mono<ServerResponse> response = handler.recordCaretakerVerification(request);

        StepVerifier.create(response)
                .assertNext(r -> assertEquals(HttpStatus.CREATED, r.statusCode()))
                .verifyComplete();
    }

    @Test
    void recordCaretakerVerification_error_returnsBadRequest() {
        when(careService.recordCaretakerVerification(eq(1L), any(CaretakerVerification.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(sampleCaretakerVerification));
        Mono<ServerResponse> response = handler.recordCaretakerVerification(request);

        StepVerifier.create(response)
                .assertNext(r -> assertEquals(HttpStatus.BAD_REQUEST, r.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCaretakerVerifications_success() {
        when(careService.getCaretakerVerifications(1L)).thenReturn(Flux.just(sampleCaretakerVerification));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getCaretakerVerifications(request);

        StepVerifier.create(response)
                .assertNext(r -> assertEquals(HttpStatus.OK, r.statusCode()))
                .verifyComplete();
    }

    @Test
    void testAccessDeniedLambdasDirectly() throws Exception {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        for (Method m : CareHandler.class.getDeclaredMethods()) {
            if (m.getName().startsWith("lambda$") && m.getParameterCount() == 1 && m.getParameterTypes()[0] == AccessDeniedException.class) {
                m.setAccessible(true);
                Object res = m.invoke(null, ex);
                if (res instanceof Mono<?> mono) {
                    StepVerifier.create(mono)
                            .expectNextCount(1)
                            .verifyComplete();
                }
            }
        }
    }
}
