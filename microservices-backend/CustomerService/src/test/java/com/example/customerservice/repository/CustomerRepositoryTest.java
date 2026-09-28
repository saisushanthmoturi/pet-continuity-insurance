package com.example.customerservice.repository;

import com.example.customerservice.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);
        sampleCustomer.setUserId(100L);
        sampleCustomer.setEmail("john.doe@example.com");
    }

    @Test
    void findByUserId_Found() {
        when(customerRepository.findByUserId(100L)).thenReturn(Mono.just(sampleCustomer));

        StepVerifier.create(customerRepository.findByUserId(100L))
                .expectNextMatches(c -> c.getUserId().equals(100L) && c.getEmail().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    void findByEmail_Found() {
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Mono.just(sampleCustomer));

        StepVerifier.create(customerRepository.findByEmail("john.doe@example.com"))
                .expectNextMatches(c -> c.getEmail().equals("john.doe@example.com"))
                .verifyComplete();
    }
}
