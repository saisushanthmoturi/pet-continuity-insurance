package com.example.customerservice.service;

import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.model.Customer;
import com.example.customerservice.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<Customer> createCustomer(CustomerRequest req) {
        if (req.userId() == null || req.fullName() == null || req.email() == null) {
            return Mono.error(new IllegalArgumentException("userId, fullName, and email are required"));
        }
        return customerRepository.findByUserId(req.userId())
                .flatMap(existing -> Mono.<Customer>error(new IllegalArgumentException("Customer profile already exists for userId: " + req.userId())))
                .switchIfEmpty(Mono.defer(() -> {
                    Customer customer = Customer.createNew(
                            req.userId(),
                            req.fullName().trim(),
                            req.email().trim().toLowerCase(),
                            req.phone(),
                            req.address(),
                            req.emergencyContact()
                    );
                    return customerRepository.save(customer)
                            .doOnSuccess(c -> log.info("Created customer profile id={}, userId={}, name={}", c.getId(), c.getUserId(), c.getFullName()));
                }));
    }

    public Mono<Customer> getById(Long id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found with id: " + id)));
    }

    public Mono<Customer> getByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found for userId: " + userId)));
    }

    public Mono<Customer> updateCustomer(Long id, CustomerRequest req) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found with id: " + id)))
                .flatMap(c -> {
                    if (req.fullName() != null && !req.fullName().isBlank()) c.setFullName(req.fullName());
                    if (req.email() != null && !req.email().isBlank()) c.setEmail(req.email());
                    if (req.phone() != null) c.setPhone(req.phone());
                    if (req.address() != null) c.setAddress(req.address());
                    if (req.emergencyContact() != null) c.setEmergencyContact(req.emergencyContact());
                    c.setUpdatedAt(LocalDateTime.now());
                    return customerRepository.save(c);
                });
    }
}
