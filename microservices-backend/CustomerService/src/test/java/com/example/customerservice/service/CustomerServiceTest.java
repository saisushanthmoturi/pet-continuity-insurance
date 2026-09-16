package com.example.customerservice.service;

import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.model.Customer;
import com.example.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer(10L, 100L, "John", "Doe", "john.doe@example.com", "1234567890",
                LocalDate.of(1985, 5, 20), 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void createCustomer_successful() {
        CustomerRequest req = new CustomerRequest(100L, "John Doe", "john.doe@example.com", "1234567890", "123 Main St", "Jane Doe");

        when(customerRepository.findByUserId(100L)).thenReturn(Mono.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(sampleCustomer));

        StepVerifier.create(customerService.createCustomer(req))
                .assertNext(customer -> {
                    assertEquals(10L, customer.getId());
                    assertEquals(100L, customer.getUserId());
                    assertEquals("john.doe@example.com", customer.getEmail());
                })
                .verifyComplete();

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_missingMandatoryFields_throwsError() {
        CustomerRequest reqNoUserId = new CustomerRequest(null, "Name", "email@test.com", null, null, null);
        StepVerifier.create(customerService.createCustomer(reqNoUserId))
                .expectError(IllegalArgumentException.class)
                .verify();

        CustomerRequest reqNoName = new CustomerRequest(100L, null, "email@test.com", null, null, null);
        StepVerifier.create(customerService.createCustomer(reqNoName))
                .expectError(IllegalArgumentException.class)
                .verify();

        CustomerRequest reqNoEmail = new CustomerRequest(100L, "Name", null, null, null, null);
        StepVerifier.create(customerService.createCustomer(reqNoEmail))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createCustomer_duplicateUserId_throwsError() {
        CustomerRequest req = new CustomerRequest(100L, "John Doe", "john.doe@example.com", "1234567890", "123 Main St", "Jane Doe");
        when(customerRepository.findByUserId(100L)).thenReturn(Mono.just(sampleCustomer));

        StepVerifier.create(customerService.createCustomer(req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("already exists"))
                .verify();

        verify(customerRepository, never()).save(any());
    }

    @Test
    void getById_found() {
        when(customerRepository.findById(10L)).thenReturn(Mono.just(sampleCustomer));

        StepVerifier.create(customerService.getById(10L))
                .assertNext(c -> {
                    assertEquals(10L, c.getId());
                    assertEquals("John Doe", c.getFullName());
                })
                .verifyComplete();
    }

    @Test
    void getById_notFound_throwsError() {
        when(customerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.getById(999L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("not found with id"))
                .verify();
    }

    @Test
    void getByUserId_found() {
        when(customerRepository.findByUserId(100L)).thenReturn(Mono.just(sampleCustomer));

        StepVerifier.create(customerService.getByUserId(100L))
                .assertNext(c -> assertEquals(100L, c.getUserId()))
                .verifyComplete();
    }

    @Test
    void getByUserId_notFound_throwsError() {
        when(customerRepository.findByUserId(999L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.getByUserId(999L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("not found for userId"))
                .verify();
    }

    @Test
    void updateCustomer_successful() {
        CustomerRequest req = new CustomerRequest(null, "Johnathan Doe", "j.doe@example.com", "9876543210", "456 Elm St", "Contact");
        when(customerRepository.findById(10L)).thenReturn(Mono.just(sampleCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(sampleCustomer));

        StepVerifier.create(customerService.updateCustomer(10L, req))
                .assertNext(c -> {
                    assertEquals("j.doe@example.com", sampleCustomer.getEmail());
                    assertEquals("9876543210", sampleCustomer.getPhone());
                })
                .verifyComplete();

        verify(customerRepository).save(sampleCustomer);
    }

    @Test
    void updateCustomer_notFound_throwsError() {
        CustomerRequest req = new CustomerRequest(null, "Name", "email@test.com", null, null, null);
        when(customerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.updateCustomer(999L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getAll_returnsFlux() {
        Customer c2 = new Customer(11L, 101L, "Sarah", "Connor", "sarah@example.com", "5555555",
                LocalDate.of(1980, 2, 2), 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        when(customerRepository.findAll()).thenReturn(Flux.just(sampleCustomer, c2));

        StepVerifier.create(customerService.getAll())
                .expectNextMatches(c -> c.getId().equals(10L))
                .expectNextMatches(c -> c.getId().equals(11L))
                .verifyComplete();
    }

    @Test
    void deleteCustomer_successful() {
        when(customerRepository.findById(10L)).thenReturn(Mono.just(sampleCustomer));
        when(customerRepository.delete(sampleCustomer)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(10L))
                .verifyComplete();

        verify(customerRepository).delete(sampleCustomer);
    }

    @Test
    void deleteCustomer_notFound_throwsError() {
        when(customerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
