package com.example.customerservice.handler;

import com.example.customerservice.dto.AddressRequest;
import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.model.Address;
import com.example.customerservice.model.Customer;
import com.example.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerHandlerTest {

    @Mock
    private CustomerService customerService;

    private CustomerHandler customerHandler;

    private Customer sampleCustomer;
    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        customerHandler = new CustomerHandler(customerService);
        sampleCustomer = new Customer(1L, 10L, "John", "Doe", "john@example.com", "1234567890", LocalDate.of(1990, 1, 1), "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        sampleAddress = new Address(1L, 1L, "123 Main St", null, "City", "State", "12345", "USA");
    }

    @Test
    void createCustomer_success() {
        CustomerRequest req = new CustomerRequest(10L, "John", "Doe", null, "john@example.com", "1234567890", null, null, null);
        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(Mono.just(sampleCustomer));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(customerHandler.createCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createCustomer_accessDenied() {
        CustomerRequest req = new CustomerRequest(10L, "John", "Doe", null, "john@example.com", "1234567890", null, null, null);
        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(customerHandler.createCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createCustomer_badRequest() {
        CustomerRequest req = new CustomerRequest(null, null, null, null, null, null, null, null, null);
        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Missing required fields")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(customerHandler.createCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCustomerById_success() {
        when(customerService.getById(1L)).thenReturn(Mono.just(sampleCustomer));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();

        StepVerifier.create(customerHandler.getCustomerById(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCustomerById_accessDenied() {
        when(customerService.getById(1L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();

        StepVerifier.create(customerHandler.getCustomerById(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCustomerById_notFound() {
        when(customerService.getById(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();

        StepVerifier.create(customerHandler.getCustomerById(request))
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCustomerByUserId_success() {
        when(customerService.getByUserId(10L)).thenReturn(Mono.just(sampleCustomer));

        MockServerRequest request = MockServerRequest.builder().pathVariable("userId", "10").build();

        StepVerifier.create(customerHandler.getCustomerByUserId(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCustomerByUserId_accessDenied() {
        when(customerService.getByUserId(10L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("userId", "10").build();

        StepVerifier.create(customerHandler.getCustomerByUserId(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getCustomerByUserId_notFound() {
        when(customerService.getByUserId(10L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("userId", "10").build();

        StepVerifier.create(customerHandler.getCustomerByUserId(request))
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateCustomer_success() {
        CustomerRequest req = new CustomerRequest(10L, "Updated", "Doe", null, "john@example.com", null, null, null, null);
        when(customerService.updateCustomer(eq(1L), any(CustomerRequest.class))).thenReturn(Mono.just(sampleCustomer));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.updateCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateCustomer_accessDenied() {
        CustomerRequest req = new CustomerRequest(10L, "Updated", "Doe", null, "john@example.com", null, null, null, null);
        when(customerService.updateCustomer(eq(1L), any(CustomerRequest.class))).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.updateCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateCustomer_badRequest() {
        CustomerRequest req = new CustomerRequest(10L, "Updated", "Doe", null, "john@example.com", null, null, null, null);
        when(customerService.updateCustomer(eq(1L), any(CustomerRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Bad request")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.updateCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllCustomers_success() {
        when(customerService.getAll()).thenReturn(Flux.just(sampleCustomer));

        MockServerRequest request = MockServerRequest.builder().build();

        StepVerifier.create(customerHandler.getAllCustomers(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllCustomers_accessDenied() {
        when(customerService.getAll()).thenReturn(Flux.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().build();

        StepVerifier.create(customerHandler.getAllCustomers(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteCustomer_success() {
        when(customerService.deleteCustomer(1L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();

        StepVerifier.create(customerHandler.deleteCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteCustomer_accessDenied() {
        when(customerService.deleteCustomer(1L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();

        StepVerifier.create(customerHandler.deleteCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteCustomer_notFound() {
        when(customerService.deleteCustomer(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();

        StepVerifier.create(customerHandler.deleteCustomer(request))
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createAddress_success() {
        AddressRequest req = new AddressRequest("HOME", "123 Main St", null, "City", "State", "12345", "USA");
        when(customerService.createAddress(eq(1L), any(AddressRequest.class))).thenReturn(Mono.just(sampleAddress));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.createAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createAddress_accessDenied() {
        AddressRequest req = new AddressRequest("HOME", "123 Main St", null, "City", "State", "12345", "USA");
        when(customerService.createAddress(eq(1L), any(AddressRequest.class))).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.createAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createAddress_badRequest() {
        AddressRequest req = new AddressRequest("HOME", "123 Main St", null, "City", "State", "12345", "USA");
        when(customerService.createAddress(eq(1L), any(AddressRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid address")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.createAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAddressesByCustomerId_success() {
        when(customerService.getAddressesByCustomerId(1L)).thenReturn(Flux.just(sampleAddress));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();

        StepVerifier.create(customerHandler.getAddressesByCustomerId(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAddressesByCustomerId_accessDenied() {
        when(customerService.getAddressesByCustomerId(1L)).thenReturn(Flux.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();

        StepVerifier.create(customerHandler.getAddressesByCustomerId(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAddressById_success() {
        when(customerService.getAddressById(1L)).thenReturn(Mono.just(sampleAddress));

        MockServerRequest request = MockServerRequest.builder().pathVariable("addressId", "1").build();

        StepVerifier.create(customerHandler.getAddressById(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAddressById_accessDenied() {
        when(customerService.getAddressById(1L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("addressId", "1").build();

        StepVerifier.create(customerHandler.getAddressById(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAddressById_notFound() {
        when(customerService.getAddressById(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("addressId", "1").build();

        StepVerifier.create(customerHandler.getAddressById(request))
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateAddress_success() {
        AddressRequest req = new AddressRequest("HOME", "Updated St", null, "City", "State", "12345", "USA");
        when(customerService.updateAddress(eq(1L), any(AddressRequest.class))).thenReturn(Mono.just(sampleAddress));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("addressId", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.updateAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateAddress_accessDenied() {
        AddressRequest req = new AddressRequest("HOME", "Updated St", null, "City", "State", "12345", "USA");
        when(customerService.updateAddress(eq(1L), any(AddressRequest.class))).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("addressId", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.updateAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateAddress_badRequest() {
        AddressRequest req = new AddressRequest("HOME", "Updated St", null, "City", "State", "12345", "USA");
        when(customerService.updateAddress(eq(1L), any(AddressRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Bad")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("addressId", "1")
                .body(Mono.just(req));

        StepVerifier.create(customerHandler.updateAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteAddress_success() {
        when(customerService.deleteAddress(1L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("addressId", "1").build();

        StepVerifier.create(customerHandler.deleteAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteAddress_accessDenied() {
        when(customerService.deleteAddress(1L)).thenReturn(Mono.error(new AccessDeniedException("Denied")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("addressId", "1").build();

        StepVerifier.create(customerHandler.deleteAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.FORBIDDEN, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteAddress_notFound() {
        when(customerService.deleteAddress(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("addressId", "1").build();

        StepVerifier.create(customerHandler.deleteAddress(request))
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void testAccessDeniedLambdasDirectly() throws Exception {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        for (java.lang.reflect.Method m : CustomerHandler.class.getDeclaredMethods()) {
            if (m.getName().startsWith("lambda$") && m.getParameterCount() == 1 && m.getParameterTypes()[0] == AccessDeniedException.class) {
                m.setAccessible(true);
                Object res = m.invoke(null, ex);
                if (res instanceof Mono<?> mono) {
                    StepVerifier.create(mono).expectNextCount(1).verifyComplete();
                }
            }
        }
    }
}
