package com.example.policyservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PerformanceLoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    private PerformanceLoggingAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new PerformanceLoggingAspect();
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        lenient().when(signature.getDeclaringType()).thenReturn((Class) String.class);
        lenient().when(signature.getName()).thenReturn("testMethod");
    }

    @Test
    void logPerformance_monoSuccess() throws Throwable {
        when(joinPoint.proceed()).thenReturn(Mono.just("result"));

        Object result = aspect.logPerformance(joinPoint);
        StepVerifier.create((Mono<String>) result)
                .expectNext("result")
                .verifyComplete();
    }

    @Test
    void logPerformance_monoError() throws Throwable {
        when(joinPoint.proceed()).thenReturn(Mono.error(new RuntimeException("Mono Error")));

        Object result = aspect.logPerformance(joinPoint);
        StepVerifier.create((Mono<String>) result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void logPerformance_fluxSuccess() throws Throwable {
        when(joinPoint.proceed()).thenReturn(Flux.just("a", "b"));

        Object result = aspect.logPerformance(joinPoint);
        StepVerifier.create((Flux<String>) result)
                .expectNext("a", "b")
                .verifyComplete();
    }

    @Test
    void logPerformance_fluxError() throws Throwable {
        when(joinPoint.proceed()).thenReturn(Flux.error(new RuntimeException("Flux Error")));

        Object result = aspect.logPerformance(joinPoint);
        StepVerifier.create((Flux<String>) result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void logPerformance_nonReactive() throws Throwable {
        when(joinPoint.proceed()).thenReturn("regular");

        Object result = aspect.logPerformance(joinPoint);
        assertEquals("regular", result);
    }

    @Test
    void logPerformance_exceptionThrown() throws Throwable {
        when(joinPoint.proceed()).thenThrow(new IllegalArgumentException("Thrown"));

        assertThrows(IllegalArgumentException.class, () -> aspect.logPerformance(joinPoint));
    }

    @Test
    void policyPackagePointcut_executes() {
        aspect.policyPackagePointcut();
    }
}
