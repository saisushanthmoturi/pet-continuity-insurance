package com.example.apigateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackHandler {

    
    @RequestMapping("/{serviceName}")
    public Mono<Map<String, Object>> serviceFallback(@PathVariable String serviceName) {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "error", "Service Unavailable",
                "message", "The " + serviceName + " service is temporarily unavailable or experiencing high latency. Circuit breaker is active.",
                "timestamp", LocalDateTime.now().toString(),
                "fallback", true
        ));
    }
}
