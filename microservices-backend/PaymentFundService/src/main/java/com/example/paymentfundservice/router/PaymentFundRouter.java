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
                        .GET("/premium", handler::getAllPayments)
                        .GET("/premium/customer/{customerId}", handler::getPaymentsByCustomerId)
                        .GET("/customer/{customerId}", handler::getPaymentsByCustomerId)
                        .GET("/premium/{id}", handler::getPaymentById)
                        .POST("/funds/create", accept(MediaType.APPLICATION_JSON), handler::createFund)
                        .POST("/funds", accept(MediaType.APPLICATION_JSON), handler::createFund)
                        .GET("/funds", handler::getAllFunds)
                        .POST("/funds/{id}/disburse-monthly", handler::disburseMonthly)
                        .POST("/funds/{id}/disburse", handler::disburseMonthly)
                        .POST("/funds/{id}/expense", accept(MediaType.APPLICATION_JSON), handler::recordExpense)
                        .GET("/funds/by-policy/{policyId}", handler::getFundByPolicyId)
                        .GET("/funds/{id}/transactions", handler::getTransactions)
                        .GET("/funds/{id}/disbursements", handler::getDisbursements)
                        .GET("/funds/{id}/expenses", handler::getExpenses)
                        .GET("/funds/{id}/history", handler::getFundHistory)
                        .GET("/funds/{id}", handler::getFundById)
                        .PUT("/funds/{id}/status", accept(MediaType.APPLICATION_JSON), handler::updateFundStatus)
                        .PUT("/funds/{id}", accept(MediaType.APPLICATION_JSON), handler::updateFund)
                        .DELETE("/funds/{id}", handler::deleteFund)
                )
                .path("/api/funds", builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::createFund)
                        .GET("", handler::getAllFunds)
                        .GET("/{id}", handler::getFundById)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON), handler::updateFund)
                        .PUT("/{id}/status", accept(MediaType.APPLICATION_JSON), handler::updateFundStatus)
                        .DELETE("/{id}", handler::deleteFund)
                        .GET("/{id}/transactions", handler::getTransactions)
                        .POST("/{id}/disburse", handler::disburseMonthly)
                        .POST("/{id}/expense", accept(MediaType.APPLICATION_JSON), handler::recordExpense)
                )
                .build();
    }
}
