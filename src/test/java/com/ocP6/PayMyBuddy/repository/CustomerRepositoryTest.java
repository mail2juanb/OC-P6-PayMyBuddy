package com.ocP6.PayMyBuddy.repository;

import com.ocP6.PayMyBuddy.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    // TODO: Transformer en test paramétrisé pour vérifier le IgnoreCase
    @Test
    void findByEmailIgnoreCase_shouldReturnCustomer() {

        // Given a known email
        String email = "user@user.com";

        // When try to find customer by email
        Optional<Customer> result = customerRepository.findByEmailIgnoreCase(email);

        // Then return Customer
        assertTrue(result.isPresent());
        assertEquals("user@user.com", result.get().getEmail());

    }


    @Test
    void findByEmailIgnoreCase_shouldReturnEmpty() {

        // Given an unknown email
        String email = "unknown@user.com";

        // When try to find customer by email
        Optional<Customer> result = customerRepository.findByEmailIgnoreCase(email);

        // Then return Empty
        assertFalse(result.isPresent());

    }

    @Test
    void findByUsername_shouldReturnCustomer(){

        // Given a username
        String username = "user";

        // When try to find customer by username
        Optional<Customer> result = customerRepository.findByUsername(username);

        // Then return Customer
        assertTrue(result.isPresent());
        assertEquals("user", result.get().getUsername());

    }


    @Test
    void findByUsername_shouldReturnEmpty() {

        // Given a username
        String username = "unknown";

        // When try to find customer by username
        Optional<Customer> result = customerRepository.findByUsername(username);

        // Then return Empty
        assertFalse(result.isPresent());

    }
}