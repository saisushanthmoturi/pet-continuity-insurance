package com.example.customerservice.service;

import com.example.customerservice.dto.AddressRequest;
import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.model.Address;
import com.example.customerservice.model.Customer;
import com.example.customerservice.repository.AddressRepository;
import com.example.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer sampleCustomer;
    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer(10L, 100L, "John", "Doe", "john.doe@example.com", "1234567890",
                LocalDate.of(1985, 5, 20), 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        sampleAddress = new Address(1L, 10L, "123 Main St", "Apt 4B", "New York", "NY", "10001", "USA");
    }

    @Test
    void createCustomer_successful() {
        CustomerRequest req = new CustomerRequest(100L, "John", "Doe", "John Doe", "john.doe@example.com", "1234567890", "123 Main St", "Jane Doe", LocalDate.of(1985, 5, 20));

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
        LocalDate newDob = LocalDate.of(1990, 1, 1);
        CustomerRequest req = new CustomerRequest(null, "Johnny", "Smith", "Johnny Smith", "j.smith@example.com", "9876543210", "456 Elm St", "Contact", newDob);
        when(customerRepository.findById(10L)).thenReturn(Mono.just(sampleCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(sampleCustomer));

        StepVerifier.create(customerService.updateCustomer(10L, req))
                .assertNext(c -> {
                    assertEquals("j.smith@example.com", sampleCustomer.getEmail());
                    assertEquals("9876543210", sampleCustomer.getPhone());
                    assertEquals("Johnny", sampleCustomer.getFirstName());
                    assertEquals("Smith", sampleCustomer.getLastName());
                    assertEquals(newDob, sampleCustomer.getDateOfBirth());
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

    @Test
    void createAddress_success() {
        AddressRequest req = new AddressRequest("HOME", "123 Main St", "Apt 4B", "New York", "NY", "10001", "USA");
        when(customerRepository.findById(10L)).thenReturn(Mono.just(sampleCustomer));
        when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(sampleAddress));

        StepVerifier.create(customerService.createAddress(10L, req))
                .assertNext(a -> {
                    assertEquals(1L, a.getAddressId());
                    assertEquals(10L, a.getCustomerId());
                })
                .verifyComplete();

        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void createAddress_missingFields_throwsError() {
        AddressRequest reqNoLine1 = new AddressRequest("HOME", null, null, "City", "State", "12345", "USA");
        StepVerifier.create(customerService.createAddress(10L, reqNoLine1))
                .expectError(IllegalArgumentException.class)
                .verify();

        AddressRequest reqNoCity = new AddressRequest("HOME", "Line1", null, null, "State", "12345", "USA");
        StepVerifier.create(customerService.createAddress(10L, reqNoCity))
                .expectError(IllegalArgumentException.class)
                .verify();

        AddressRequest reqNoState = new AddressRequest("HOME", "Line1", null, "City", null, "12345", "USA");
        StepVerifier.create(customerService.createAddress(10L, reqNoState))
                .expectError(IllegalArgumentException.class)
                .verify();

        AddressRequest reqNoZip = new AddressRequest("HOME", "Line1", null, "City", "State", null, "USA");
        StepVerifier.create(customerService.createAddress(10L, reqNoZip))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createAddress_customerNotFound_throwsError() {
        AddressRequest req = new AddressRequest("HOME", "Line1", null, "City", "State", "12345", "USA");
        when(customerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.createAddress(999L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getAddressesByCustomerId_returnsFlux() {
        when(addressRepository.findByCustomerId(10L)).thenReturn(Flux.just(sampleAddress));

        StepVerifier.create(customerService.getAddressesByCustomerId(10L))
                .assertNext(a -> assertEquals(10L, a.getCustomerId()))
                .verifyComplete();
    }

    @Test
    void getAddressById_found() {
        when(addressRepository.findById(1L)).thenReturn(Mono.just(sampleAddress));

        StepVerifier.create(customerService.getAddressById(1L))
                .assertNext(a -> assertEquals(1L, a.getAddressId()))
                .verifyComplete();
    }

    @Test
    void getAddressById_notFound_throwsError() {
        when(addressRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.getAddressById(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updateAddress_success() {
        AddressRequest req = new AddressRequest("WORK", "456 Elm St", "Suite 2", "Albany", "NY", "12207", "USA");
        when(addressRepository.findById(1L)).thenReturn(Mono.just(sampleAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(sampleAddress));

        StepVerifier.create(customerService.updateAddress(1L, req))
                .assertNext(a -> {
                    assertEquals("WORK", sampleAddress.getAddressType());
                    assertEquals("456 Elm St", sampleAddress.getLine1());
                    assertEquals("Suite 2", sampleAddress.getLine2());
                    assertEquals("Albany", sampleAddress.getCity());
                    assertEquals("NY", sampleAddress.getState());
                    assertEquals("12207", sampleAddress.getPostalCode());
                    assertEquals("USA", sampleAddress.getCountry());
                })
                .verifyComplete();

        verify(addressRepository).save(sampleAddress);
    }

    @Test
    void updateAddress_notFound_throwsError() {
        AddressRequest req = new AddressRequest("HOME", "L1", null, "City", "State", "12345", "USA");
        when(addressRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.updateAddress(999L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteAddress_success() {
        when(addressRepository.findById(1L)).thenReturn(Mono.just(sampleAddress));
        when(addressRepository.delete(sampleAddress)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteAddress(1L))
                .verifyComplete();

        verify(addressRepository).delete(sampleAddress);
    }

    @Test
    void deleteAddress_notFound_throwsError() {
        when(addressRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteAddress(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
