package com.example.paymentfundservice.router;

import com.example.paymentfundservice.handler.PaymentFundHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PaymentFundRouter {

    @Bean
    public RouterFunction<ServerResponse> paymentFundRoutes(PaymentFundHandler handler) {
        return RouterFunctions.route()
                .path("/api/payments", builder -> builder
                        .POST("/premium", accept(MediaType.APPLICATION_JSON), handler::payPremium)
                        .POST("/funds/create", accept(MediaType.APPLICATION_JSON), handler::createFund)
                        .POST("/funds/{id}/disburse-monthly", handler::disburseMonthly)
                        .POST("/funds/{id}/expense", accept(MediaType.APPLICATION_JSON), handler::recordExpense)
                        .GET("/funds/by-policy/{policyId}", handler::getFundByPolicyId)
                        .GET("/funds/{id}/transactions", handler::getTransactions)
                        .GET("/funds/{id}", handler::getFundById)
                )
                .build();
    }
}
