package com.example.customerservice.model;

import com.example.customerservice.dto.AddressRequest;
import com.example.customerservice.dto.CustomerRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerModelAndDtoTest {

    @Test
    void testCustomer() {
        Customer c = new Customer();
        assertNotNull(c);

        LocalDate dob = LocalDate.of(1995, 5, 20);
        LocalDateTime now = LocalDateTime.now();

        c.setId(1L);
        c.setCustomerId(1L);
        c.setUserId(10L);
        c.setFirstName("Alice");
        c.setLastName("Smith");
        c.setContactEmail("alice@example.com");
        c.setEmail("alice@example.com");
        c.setPhone("1234567890");
        c.setDateOfBirth(dob);
        c.setStatus("ACTIVE");
        c.setAddress("123 Main St");
        c.setEmergencyContact("Bob Smith");
        c.setCreatedAt(now);
        c.setUpdatedAt(now);

        assertEquals(1L, c.getId());
        assertEquals(1L, c.getCustomerId());
        assertEquals(10L, c.getUserId());
        assertEquals("Alice", c.getFirstName());
        assertEquals("Smith", c.getLastName());
        assertEquals("Alice Smith", c.getFullName());
        assertEquals("alice@example.com", c.getContactEmail());
        assertEquals("alice@example.com", c.getEmail());
        assertEquals("1234567890", c.getPhone());
        assertEquals(dob, c.getDateOfBirth());
        assertEquals("ACTIVE", c.getStatus());
        assertEquals("123 Main St", c.getAddress());
        assertEquals("Bob Smith", c.getEmergencyContact());
        assertEquals(now, c.getCreatedAt());
        assertEquals(now, c.getUpdatedAt());

        c.setFullName("SingleWordName");
        assertEquals("SingleWordName", c.getFirstName());
        assertEquals("", c.getLastName());

        c.setFullName("Two Words");
        assertEquals("Two", c.getFirstName());
        assertEquals("Words", c.getLastName());

        c.setFirstName(null);
        c.setLastName(null);
        assertEquals("", c.getFullName());

        c.setLastName("OnlyLast");
        assertEquals("OnlyLast", c.getFullName());

        Customer full1 = new Customer(1L, 10L, "F", "L", "E", "P", dob, "ACT", now, now);
        assertEquals(1L, full1.getId());

        Customer full2 = new Customer(1L, 10L, "F", "L", "E", "P", dob, 5L, "ACT", now, now);
        assertEquals(1L, full2.getId());

        Customer fromNew1 = Customer.createNew(10L, "F", "L", null, "e@e.com", "123", "addr", "em", dob);
        assertEquals("F", fromNew1.getFirstName());
        assertEquals("L", fromNew1.getLastName());

        Customer fromNew2 = Customer.createNew(10L, null, null, "First Last", "e@e.com", "123", "addr", "em", null);
        assertEquals("First", fromNew2.getFirstName());
        assertEquals("Last", fromNew2.getLastName());

        Customer fromNew3 = Customer.createNew(10L, null, null, "Single", "e@e.com", "123", "addr", "em", null);
        assertEquals("Single", fromNew3.getFirstName());
        assertEquals("Single", fromNew3.getLastName());

        Customer fromNew4 = Customer.createNew(10L, "Full Name Only", "e@e.com", "123", "addr", "em");
        assertEquals("Full Name", fromNew4.getFirstName());
        assertEquals("Only", fromNew4.getLastName());
    }

    @Test
    void testAddress() {
        Address a = new Address();
        assertNotNull(a);
        assertEquals("PRIMARY", a.getAddressType());
        assertEquals("USA", a.getCountry());

        
        a.setAddressId(1L);
        a.setCustomerId(10L);
        a.setAddressType("OFFICE");
        a.setLine1("Line 1");
        a.setLine2("Line 2");
        a.setCity("New York");
        a.setState("NY");
        a.setPostalCode("10001");
        a.setCountry("USA");

        
        assertEquals(1L, a.getAddressId());
        assertEquals(10L, a.getCustomerId());
        assertEquals("OFFICE", a.getAddressType());
        assertEquals("Line 1", a.getLine1());
        assertEquals("Line 2", a.getLine2());
        assertEquals("New York", a.getCity());
        assertEquals("NY", a.getState());
        assertEquals("10001", a.getPostalCode());
        assertEquals("USA", a.getCountry());

        Address full = new Address(2L, 10L, "L1", "L2", "City", "State", "12345", "USA");
        assertEquals(2L, full.getAddressId());
    }

    @Test
    void testDtos() {
        LocalDate dob = LocalDate.of(1990, 1, 1);
        CustomerRequest cr = new CustomerRequest(10L, "First", "Last", "First Last", "e@e.com", "123", "Addr", "Em", dob);
        assertEquals(10L, cr.userId());
        assertEquals("First", cr.firstName());
        assertEquals("Last", cr.lastName());
        assertEquals("First Last", cr.fullName());
        assertEquals("e@e.com", cr.email());
        assertEquals("123", cr.phone());
        assertEquals("Addr", cr.address());
        assertEquals("Em", cr.emergencyContact());
        assertEquals(dob, cr.dateOfBirth());

        AddressRequest ar = new AddressRequest("HOME", "L1", "L2", "City", "State", "12345", "USA");
        assertEquals("HOME", ar.addressType());
        assertEquals("L1", ar.line1());
        assertEquals("L2", ar.line2());
        assertEquals("City", ar.city());
        assertEquals("State", ar.state());
        assertEquals("12345", ar.postalCode());
        assertEquals("USA", ar.country());
    }
}
