package com.example.petservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class PerformanceLoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    private PerformanceLoggingAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new PerformanceLoggingAspect();
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn((Class) PetServiceTestClass.class);
        when(signature.getName()).thenReturn("testMethod");
    }

    private static class PetServiceTestClass {
    }

    @Test
    void testPetPackagePointcut() {
        aspect.petPackagePointcut();
    }

    @Test
    void testLogPerformance_monoSuccess() throws Throwable {
        when(joinPoint.proceed()).thenReturn(Mono.just("success"));

        Object result = aspect.logPerformance(joinPoint);
        assertTrue(result instanceof Mono);

        StepVerifier.create((Mono<Object>) (Mono<?>) result)
                .expectNext("success")
                .verifyComplete();
    }

    @Test
    void testLogPerformance_monoError() throws Throwable {
        when(joinPoint.proceed()).thenReturn(Mono.error(new RuntimeException("fail")));

        Object result = aspect.logPerformance(joinPoint);
        assertTrue(result instanceof Mono);

        StepVerifier.create((Mono<Object>) (Mono<?>) result)
                .expectErrorMessage("fail")
                .verify();
    }

    @Test
    void testLogPerformance_fluxSuccess() throws Throwable {
        when(joinPoint.proceed()).thenReturn(Flux.just("item1", "item2"));

        Object result = aspect.logPerformance(joinPoint);
        assertTrue(result instanceof Flux);

        StepVerifier.create((Flux<Object>) (Flux<?>) result)
                .expectNext("item1", "item2")
                .verifyComplete();
    }

    @Test
    void testLogPerformance_fluxError() throws Throwable {
        when(joinPoint.proceed()).thenReturn(Flux.error(new RuntimeException("flux error")));

        Object result = aspect.logPerformance(joinPoint);
        assertTrue(result instanceof Flux);

        StepVerifier.create((Flux<Object>) (Flux<?>) result)
                .expectErrorMessage("flux error")
                .verify();
    }

    @Test
    void testLogPerformance_regularObject() throws Throwable {
        when(joinPoint.proceed()).thenReturn("plainString");

        Object result = aspect.logPerformance(joinPoint);
        assertEquals("plainString", result);
    }

    @Test
    void testLogPerformance_slowExecution() throws Throwable {
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(520);
            return "slowResult";
        });

        Object result = aspect.logPerformance(joinPoint);
        assertEquals("slowResult", result);
    }

    @Test
    void testLogPerformance_throwsException() throws Throwable {
        when(joinPoint.proceed()).thenThrow(new IllegalArgumentException("sync error"));

        assertThrows(IllegalArgumentException.class, () -> aspect.logPerformance(joinPoint));
    }
}
