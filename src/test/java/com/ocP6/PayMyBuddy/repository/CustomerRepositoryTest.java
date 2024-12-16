package com.ocP6.PayMyBuddy.repository;

import com.ocP6.PayMyBuddy.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @ParameterizedTest
    @MethodSource("provideValidEmail")
    void findByEmailIgnoreCase_shouldReturnCustomer(String email) {

        // Given a known email by provideValidEmail

        // When try to find customer by email
        Optional<Customer> result = customerRepository.findByEmailIgnoreCase(email);

        // Then return Customer
        assertTrue(result.isPresent());
        assertEquals("user@user.com", result.get().getEmail());

    }

    // Returns Valid email
    static Stream<String> provideValidEmail() {
        String email1 = "user@user.com";
        String email2 = "User@user.com";
        String email3 = "user@User.com";

        return Stream.of(email1, email2, email3);
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