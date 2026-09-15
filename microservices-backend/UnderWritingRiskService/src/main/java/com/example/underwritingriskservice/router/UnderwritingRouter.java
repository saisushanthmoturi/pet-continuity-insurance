package com.example.underwritingriskservice.router;

import com.example.underwritingriskservice.handler.UnderwritingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class UnderwritingRouter {

    @Bean
    public RouterFunction<ServerResponse> underwritingRoutes(UnderwritingHandler handler) {
        return RouterFunctions.route()
                .path("/api/underwriting", builder -> builder
                        .POST("/quotes", accept(MediaType.APPLICATION_JSON), handler::generateQuote)
                        .GET("/quotes/{id}", handler::getQuoteById)
                        .GET("/risk-monitoring/{petId}", handler::getRiskMonitoring)
                )
                .build();
    }
}
