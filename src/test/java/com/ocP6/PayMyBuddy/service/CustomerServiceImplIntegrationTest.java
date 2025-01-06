package com.ocP6.PayMyBuddy.service;


import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
class CustomerServiceImplIntegrationTest {

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;




    @Test
    void createCustomer_shouldAddCustomer() {

        //Given a new customer
        final String username = "John";
        final String email = "john.snow@test.test";
        final String rawPassword = "azerty123";

        // When the user try to be registred
        customerService.createCustomer(username, email, rawPassword);

        // Then
        BigDecimal zero = BigDecimal.ZERO.setScale(2);

        assertThat(customerRepository.findByUsername(username))
                .isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getEmail()).isEqualTo(email);
                    assertThat(user.getBalance()).isEqualTo(zero);
                    assertTrue(passwordEncoder.matches(rawPassword, user.getPassword()));
                } );
    }

    @Test
    @Transactional
    void getConnectionsByUsername_shouldReturnCustomerConnectionList() {

        // Given a known username
        final String username = "user";

        // When try to get connections // Then return a list of customer with this username
        assertThat(customerRepository.findByUsername(username))
                .isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getUsername()).isEqualTo(username);
                    assertThat(user.getConnections()).isNotNull();
                    assertThat(user.getConnections().size()).isEqualTo(2);
                } );

    }



    @Test
    @Transactional
    void getTransactionsByUsername_shouldReturnTransactionList() {

        // Given a known username
        final String username = "user";

        // When try to get transactions // Then return a list of transaction with this username
        assertThat(customerRepository.findByUsername(username))
                .isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getUsername()).isEqualTo(username);
                    assertThat(user.getSentTransactions()).isNotNull();
                    assertThat(user.getSentTransactions().size()).isEqualTo(2);
                } );
    }


    @Test
    void getEmailByUsername_shouldReturnEmail() {

        // Given a known username
        final String username = "user";
        final String email = "user@user.com";

        // When try to get email // Then return the email string about this username
        assertThat(customerRepository.findByUsername(username))
                .isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getUsername()).isEqualTo(username);
                    assertThat(user.getEmail()).isEqualTo(email);
                } );
    }


    @Test
    @Transactional
    void addConnection_shouldAddConnection() {

        // Given a known username and email to add
        final String username = "user";
        final String email = "copain@copain.com";

        // When try to add connection
        customerService.addConnection(username, email);

        // Then
        Optional<Customer> customer = customerRepository.findByUsername(username);
        assertThat(customer)
                .isPresent()
                .get()
                .satisfies(user -> {
                   assertThat(user.getUsername()).isEqualTo(username);
                   assertThat(user.getConnections()).isNotNull();
                   assertThat(user.getConnections())
                           .extracting("email")
                           .contains(email);
                });
    }


    @Test
    void addConnection_shouldThrowNotFoundException_whenEmailNotFound() {

        // Given a known username and unknown email
        final String username = "user";
        final String email = "unknownCopain@copain.com";

        // When try to add connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.addConnection(username, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");
    }


    @Test
    void addConnection_shouldThrowConflictException_whenSelfUsername() {

        // Given a known username and email
        final String username = "user";
        final String email = "user@user.com";

        // When try to add connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.addConnection(username, email))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("yourself");
    }


    @Test
    @Transactional
    void addConnection_shouldThrowConflictException_whenEmailAlreadyExistInList() {

        // Given a known username and email
        final String username = "user";
        final String email = "friend@friend.com";

        // When try to add connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.addConnection(username, email))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Already");
    }

}