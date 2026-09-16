package com.example.customerservice.repository;

import com.example.customerservice.model.Address;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AddressRepository extends R2dbcRepository<Address, Long> {
    Flux<Address> findByCustomerId(Long customerId);
}
