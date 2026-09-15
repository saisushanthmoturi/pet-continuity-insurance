package com.example.customerservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class PerformanceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceLoggingAspect.class);

    @Pointcut("execution(* com.example.customerservice..*(..)) && !within(com.example.customerservice.aspect..*)")
    public void customerPackagePointcut() {
    }

    @Around("customerPackagePointcut()")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();

            if (result instanceof Mono<?> mono) {
                return mono
                        .doOnSuccess(val -> {
                            long duration = System.currentTimeMillis() - start;
                            logPerformanceResult(className, methodName, duration, "SUCCESS");
                        })
                        .doOnError(error -> {
                            long duration = System.currentTimeMillis() - start;
                            log.error("[PERF-AOP] {}.{} failed in {}ms with error: {}",
                                    className, methodName, duration, error.getMessage());
                        });
            } else if (result instanceof Flux<?> flux) {
                return flux
                        .doOnComplete(() -> {
                            long duration = System.currentTimeMillis() - start;
                            logPerformanceResult(className, methodName, duration, "SUCCESS");
                        })
                        .doOnError(error -> {
                            long duration = System.currentTimeMillis() - start;
                            log.error("[PERF-AOP] {}.{} failed in {}ms with error: {}",
                                    className, methodName, duration, error.getMessage());
                        });
            } else {
                long duration = System.currentTimeMillis() - start;
                logPerformanceResult(className, methodName, duration, "SUCCESS");
                return result;
            }
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - start;
            log.error("[PERF-AOP] {}.{} threw exception in {}ms: {}", className, methodName, duration, t.getMessage());
            throw t;
        }
    }

    private void logPerformanceResult(String className, String methodName, long duration, String status) {
        if (duration > 500) {
            log.warn("[PERF-AOP-SLOW] {}.{} took {}ms [Status: {}]", className, methodName, duration, status);
        } else {
            log.info("[PERF-AOP] {}.{} took {}ms [Status: {}]", className, methodName, duration, status);
        }
    }
}
