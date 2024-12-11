package com.ocP6.PayMyBuddy.repository;

import com.ocP6.PayMyBuddy.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findByEmailIgnoreCase_shouldReturnCustomer() {

        // Given a known email
        String email = "user@user.com";

        // When try to find customer by email
        Optional<Customer> result = customerRepository.findByEmailIgnoreCase(email);

        // Then check that customer founded
        assertTrue(result.isPresent());
        assertEquals("user@user.com", result.get().getEmail());

    }


    @Test
    void findByEmailIgnoreCase_shouldReturnEmpty() {

        // Given an unknown email
        String email = "unknown@user.com";
        String errorMessage = "A_Remplir";

        // When try to find customer by email
        Optional<Customer> result = customerRepository.findByEmailIgnoreCase(email);

        // Then check that customer founded
        assertFalse(result.isPresent());
        // TODO: Vérifier qu'une NotFoundException est levée
        // TODO: Vérifier que le message renvoyé est conforme

    }

}