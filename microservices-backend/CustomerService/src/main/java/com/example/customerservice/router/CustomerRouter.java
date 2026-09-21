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
                        .GET("", handler::getAllCustomers)
                        .GET("/user/{userId}", handler::getCustomerByUserId)
                        .POST("/{id}/addresses", accept(MediaType.APPLICATION_JSON), handler::createAddress)
                        .GET("/{id}/addresses", handler::getAddressesByCustomerId)
                        .GET("/addresses/{addressId}", handler::getAddressById)
                        .PUT("/addresses/{addressId}", accept(MediaType.APPLICATION_JSON), handler::updateAddress)
                        .DELETE("/addresses/{addressId}", handler::deleteAddress)
                        .GET("/{id}", handler::getCustomerById)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON), handler::updateCustomer)
                        .DELETE("/{id}", handler::deleteCustomer)
                )
                .build();
    }
}
