package com.example.customerservice.router;

import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.handler.CustomerHandler;
import com.example.customerservice.model.Customer;
import com.example.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRouterTest {

    @Mock
    private CustomerService customerService;

    private WebTestClient webTestClient;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        CustomerHandler handler = new CustomerHandler(customerService);
        CustomerRouter router = new CustomerRouter();
        RouterFunction<ServerResponse> routes = router.customerRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);
        sampleCustomer.setUserId(100L);
        sampleCustomer.setFirstName("John");
        sampleCustomer.setLastName("Doe");
        sampleCustomer.setEmail("john.doe@example.com");
        sampleCustomer.setPhone("+1-555-0199");
        sampleCustomer.setDateOfBirth(LocalDate.of(1985, 6, 15));
    }

    @Test
    void createCustomer_Success() {
        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(Mono.just(sampleCustomer));

        CustomerRequest request = new CustomerRequest(100L, "John", "Doe", "John Doe", "john.doe@example.com", "+1-555-0199", "123 Main St", "Jane Doe", LocalDate.of(1985, 6, 15));

        webTestClient.post()
                .uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.email").isEqualTo("john.doe@example.com");
    }

    @Test
    void getCustomerById_Found() {
        when(customerService.getById(1L)).thenReturn(Mono.just(sampleCustomer));

        webTestClient.get()
                .uri("/api/customers/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.email").isEqualTo("john.doe@example.com");
    }

    @Test
    void getCustomerById_NotFound() {
        when(customerService.getById(99L)).thenReturn(Mono.error(new IllegalArgumentException("Customer not found with id: 99")));

        webTestClient.get()
                .uri("/api/customers/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getCustomerByUserId_Found() {
        when(customerService.getByUserId(100L)).thenReturn(Mono.just(sampleCustomer));

        webTestClient.get()
                .uri("/api/customers/user/100")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo(100);
    }

    @Test
    void getAllCustomers_ReturnsList() {
        when(customerService.getAll()).thenReturn(Flux.just(sampleCustomer));

        webTestClient.get()
                .uri("/api/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1);
    }
}
