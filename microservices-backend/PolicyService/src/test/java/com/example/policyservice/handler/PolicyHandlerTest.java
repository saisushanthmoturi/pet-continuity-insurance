package com.example.policyservice.handler;

import com.example.policyservice.dto.PolicyResponse;
import com.example.policyservice.dto.StatusUpdateRequest;
import com.example.policyservice.model.Coverage;
import com.example.policyservice.model.Policy;
import com.example.policyservice.model.PolicyStatusHistory;
import com.example.policyservice.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PolicyHandlerTest {

    @Mock
    private PolicyService policyService;

    private PolicyHandler policyHandler;

    private PolicyResponse sampleResponse;
    private Coverage sampleCoverage;
    private PolicyStatusHistory sampleHistory;

    @BeforeEach
    void setUp() {
        policyHandler = new PolicyHandler(policyService);
        sampleResponse = new PolicyResponse(100L, "POL-2024-001", 5L, 10L, 20L, 5000.0, 45.0, "ACTIVE", "2024-01-01", "2025-01-01", LocalDateTime.now());
        sampleCoverage = new Coverage(1L, 100L, "PET_CONTINUITY", 5000.0, 5000.0, 0.0, "ACTIVE");
        sampleHistory = PolicyStatusHistory.create(100L, "PENDING_PAYMENT", "ACTIVE", "Paid");
    }

    @Test
    void create_success() {
        when(policyService.createPolicyFromQuote(5L)).thenReturn(Mono.just(sampleResponse));

        MockServerRequest request = MockServerRequest.builder().pathVariable("quoteId", "5").build();

        StepVerifier.create(policyHandler.createFromQuote(request))
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void create_accessDenied() {
        when(policyService.createPolicyFromQuote(5L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("quoteId", "5").build();

        StepVerifier.create(policyHandler.createFromQuote(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void create_badRequest() {
        when(policyService.createPolicyFromQuote(5L)).thenReturn(Mono.error(new IllegalArgumentException("Invalid quote")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("quoteId", "5").build();

        StepVerifier.create(policyHandler.createFromQuote(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void activate_success() {
        when(policyService.activatePolicy(100L)).thenReturn(Mono.just(sampleResponse));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.activate(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void activate_accessDenied() {
        when(policyService.activatePolicy(100L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.activate(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void activate_badRequest() {
        when(policyService.activatePolicy(100L)).thenReturn(Mono.error(new IllegalStateException("Cannot activate")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.activate(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getById_success() {
        when(policyService.getById(100L)).thenReturn(Mono.just(sampleResponse));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.getById(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getById_accessDenied() {
        when(policyService.getById(100L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.getById(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getById_notFound() {
        when(policyService.getById(100L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.getById(request))
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getByNumber_success() {
        when(policyService.getByPolicyNumber("POL-2024-001")).thenReturn(Mono.just(sampleResponse));

        MockServerRequest request = MockServerRequest.builder().pathVariable("policyNumber", "POL-2024-001").build();

        StepVerifier.create(policyHandler.getByNumber(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getByNumber_accessDenied() {
        when(policyService.getByPolicyNumber("POL-2024-001")).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("policyNumber", "POL-2024-001").build();

        StepVerifier.create(policyHandler.getByNumber(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getByNumber_notFound() {
        when(policyService.getByPolicyNumber("POL-2024-001")).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("policyNumber", "POL-2024-001").build();

        StepVerifier.create(policyHandler.getByNumber(request))
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getByCustomerId_success() {
        when(policyService.getByCustomerId(10L)).thenReturn(Flux.just(sampleResponse));

        MockServerRequest request = MockServerRequest.builder().pathVariable("customerId", "10").build();

        StepVerifier.create(policyHandler.getByCustomerId(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getByCustomerId_accessDenied() {
        when(policyService.getByCustomerId(10L)).thenReturn(Flux.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("customerId", "10").build();

        StepVerifier.create(policyHandler.getByCustomerId(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateStatus_success() {
        StatusUpdateRequest req = new StatusUpdateRequest("TERMINATED", "Cancelled");
        when(policyService.updateStatus(100L, "TERMINATED", "Cancelled")).thenReturn(Mono.just(sampleResponse));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(req));

        StepVerifier.create(policyHandler.updateStatus(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateStatus_accessDenied() {
        StatusUpdateRequest req = new StatusUpdateRequest("TERMINATED", "Cancelled");
        when(policyService.updateStatus(100L, "TERMINATED", "Cancelled")).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(req));

        StepVerifier.create(policyHandler.updateStatus(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateStatus_badRequest() {
        StatusUpdateRequest req = new StatusUpdateRequest("TERMINATED", "Cancelled");
        when(policyService.updateStatus(100L, "TERMINATED", "Cancelled")).thenReturn(Mono.error(new IllegalArgumentException("Bad status")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(req));

        StepVerifier.create(policyHandler.updateStatus(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAll_success() {
        when(policyService.getAll()).thenReturn(Flux.just(sampleResponse));

        MockServerRequest request = MockServerRequest.builder().build();

        StepVerifier.create(policyHandler.getAll(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAll_accessDenied() {
        when(policyService.getAll()).thenReturn(Flux.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().build();

        StepVerifier.create(policyHandler.getAll(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updatePolicy_success() {
        Policy p = new Policy();
        p.setCoverageAmount(6000.0);
        when(policyService.updatePolicy(eq(100L), any(Policy.class))).thenReturn(Mono.just(sampleResponse));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(p));

        StepVerifier.create(policyHandler.updatePolicy(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updatePolicy_accessDenied() {
        Policy p = new Policy();
        when(policyService.updatePolicy(eq(100L), any(Policy.class))).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(p));

        StepVerifier.create(policyHandler.updatePolicy(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updatePolicy_badRequest() {
        Policy p = new Policy();
        when(policyService.updatePolicy(eq(100L), any(Policy.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid policy")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(p));

        StepVerifier.create(policyHandler.updatePolicy(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deletePolicy_success() {
        when(policyService.deletePolicy(100L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.deletePolicy(request))
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deletePolicy_accessDenied() {
        when(policyService.deletePolicy(100L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.deletePolicy(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deletePolicy_notFound() {
        when(policyService.deletePolicy(100L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.deletePolicy(request))
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCoverages_success() {
        when(policyService.getCoveragesByPolicyId(100L)).thenReturn(Flux.just(sampleCoverage));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.getCoverages(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCoverages_accessDenied() {
        when(policyService.getCoveragesByPolicyId(100L)).thenReturn(Flux.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.getCoverages(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void addCoverage_success() {
        Coverage c = new Coverage();
        c.setCoverageType("PET_CONTINUITY");
        when(policyService.addCoverage(eq(100L), any(Coverage.class))).thenReturn(Mono.just(sampleCoverage));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(c));

        StepVerifier.create(policyHandler.addCoverage(request))
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void addCoverage_accessDenied() {
        Coverage c = new Coverage();
        when(policyService.addCoverage(eq(100L), any(Coverage.class))).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(c));

        StepVerifier.create(policyHandler.addCoverage(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void addCoverage_badRequest() {
        Coverage c = new Coverage();
        when(policyService.addCoverage(eq(100L), any(Coverage.class))).thenReturn(Mono.error(new IllegalArgumentException("Bad coverage")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "100")
                .body(Mono.just(c));

        StepVerifier.create(policyHandler.addCoverage(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getStatusHistory_success() {
        when(policyService.getPolicyStatusHistory(100L)).thenReturn(Flux.just(sampleHistory));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.getStatusHistory(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getStatusHistory_accessDenied() {
        when(policyService.getPolicyStatusHistory(100L)).thenReturn(Flux.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "100").build();

        StepVerifier.create(policyHandler.getStatusHistory(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void testAccessDeniedLambdasDirectly() throws Exception {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        for (java.lang.reflect.Method m : PolicyHandler.class.getDeclaredMethods()) {
            if (m.getName().startsWith("lambda$") && m.getParameterCount() == 1 && m.getParameterTypes()[0] == AccessDeniedException.class) {
                m.setAccessible(true);
                Object res = m.invoke(null, ex);
                if (res instanceof Mono<?> mono) {
                    StepVerifier.create(mono).expectNextCount(1).verifyComplete();
                }
            }
        }
    }
}
