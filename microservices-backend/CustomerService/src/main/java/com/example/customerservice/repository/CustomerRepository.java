package com.example.customerservice.repository;

import com.example.customerservice.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    Mono<Customer> findByUserId(Long userId);
    Mono<Customer> findByEmail(String email);
}
