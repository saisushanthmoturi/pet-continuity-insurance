package com.example.customerservice.service;

import com.example.customerservice.dto.AddressRequest;
import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.model.Address;
import com.example.customerservice.model.Customer;
import com.example.customerservice.repository.AddressRepository;
import com.example.customerservice.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Customer> createCustomer(CustomerRequest req) {
        if (req.userId() == null || req.email() == null || ((req.fullName() == null || req.fullName().isBlank()) && (req.firstName() == null || req.firstName().isBlank()))) {
            return Mono.error(new IllegalArgumentException("userId, email, and name (firstName or fullName) are required"));
        }
        return customerRepository.findByUserId(req.userId())
                .flatMap(existing -> Mono.<Customer>error(new IllegalArgumentException("Customer profile already exists for userId: " + req.userId())))
                .switchIfEmpty(Mono.defer(() -> {
                    Customer customer = Customer.createNew(
                            req.userId(),
                            req.firstName(),
                            req.lastName(),
                            req.fullName(),
                            req.email().trim().toLowerCase(),
                            req.phone(),
                            req.address(),
                            req.emergencyContact(),
                            req.dateOfBirth()
                    );
                    return customerRepository.save(customer)
                            .doOnSuccess(c -> log.info("Created customer profile id={}, userId={}, name={}", c.getId(), c.getUserId(), c.getFullName()));
                }));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Customer> getById(Long id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found with id: " + id)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Customer> getByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found for userId: " + userId)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Customer> updateCustomer(Long id, CustomerRequest req) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found with id: " + id)))
                .flatMap(c -> {
                    if (req.firstName() != null && !req.firstName().isBlank()) c.setFirstName(req.firstName().trim());
                    if (req.lastName() != null && !req.lastName().isBlank()) c.setLastName(req.lastName().trim());
                    if (req.fullName() != null && !req.fullName().isBlank()) c.setFullName(req.fullName().trim());
                    if (req.email() != null && !req.email().isBlank()) c.setEmail(req.email().trim());
                    if (req.phone() != null) c.setPhone(req.phone().trim());
                    if (req.address() != null) c.setAddress(req.address().trim());
                    if (req.emergencyContact() != null) c.setEmergencyContact(req.emergencyContact().trim());
                    if (req.dateOfBirth() != null) c.setDateOfBirth(req.dateOfBirth());
                    c.setUpdatedAt(LocalDateTime.now());
                    return customerRepository.save(c);
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<Customer> getAll() {
        return customerRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteCustomer(Long id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found with id: " + id)))
                .flatMap(customerRepository::delete)
                .doOnSuccess(v -> log.info("Deleted customer profile id={}", id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Address> createAddress(Long customerId, AddressRequest req) {
        if (req.line1() == null || req.city() == null || req.state() == null || req.postalCode() == null) {
            return Mono.error(new IllegalArgumentException("line1, city, state, and postalCode are required"));
        }
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found with id: " + customerId)))
                .flatMap(c -> {
                    Address address = new Address(
                            null,
                            customerId,
                            req.line1().trim(),
                            req.line2() != null ? req.line2().trim() : null,
                            req.city().trim(),
                            req.state().trim(),
                            req.postalCode().trim(),
                            req.country() != null ? req.country().trim() : "USA"
                    );
                    if (req.addressType() != null && !req.addressType().isBlank()) {
                        address.setAddressType(req.addressType().trim().toUpperCase());
                    }
                    return addressRepository.save(address)
                            .doOnSuccess(a -> log.info("Created address id={} for customerId={}", a.getAddressId(), customerId));
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_UNDERWRITER', 'ROLE_INTERNAL_SERVICE')")
    public Flux<Address> getAddressesByCustomerId(Long customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_UNDERWRITER', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Address> getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Address not found with id: " + addressId)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Address> updateAddress(Long addressId, AddressRequest req) {
        return addressRepository.findById(addressId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Address not found with id: " + addressId)))
                .flatMap(a -> {
                    if (req.addressType() != null) a.setAddressType(req.addressType().trim().toUpperCase());
                    if (req.line1() != null) a.setLine1(req.line1().trim());
                    if (req.line2() != null) a.setLine2(req.line2().trim());
                    if (req.city() != null) a.setCity(req.city().trim());
                    if (req.state() != null) a.setState(req.state().trim());
                    if (req.postalCode() != null) a.setPostalCode(req.postalCode().trim());
                    if (req.country() != null) a.setCountry(req.country().trim());
                    return addressRepository.save(a);
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<Void> deleteAddress(Long addressId) {
        return addressRepository.findById(addressId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Address not found with id: " + addressId)))
                .flatMap(addressRepository::delete)
                .doOnSuccess(v -> log.info("Deleted address id={}", addressId));
    }
}
