package com.example.customerservice.router;

import com.example.customerservice.handler.CustomerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class CustomerRouter {

    @Bean
    public RouterFunction<ServerResponse> customerRoutes(CustomerHandler handler) {
        return RouterFunctions.route()
                .path("/api/customers", builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::createCustomer)
                        .GET("/user/{userId}", handler::getCustomerByUserId)
                        .GET("/{id}", handler::getCustomerById)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON), handler::updateCustomer)
                )
                .build();
    }
}
