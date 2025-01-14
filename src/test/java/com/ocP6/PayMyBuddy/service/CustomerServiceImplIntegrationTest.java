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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@Transactional
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
    void createCustomer_shouldThrowConflictException_whenEmailAlreadyExists() {

        // Given a customer already registred
        final String username = "user";
        final String email = "user@user.com";
        final String rawPassword = "user";

        // When the user try to be registred // Then a Conflict Exception is thrown
        assertThatThrownBy(() -> customerService.createCustomer(username, email, rawPassword))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exist");
    }



    //FIXME: Supprimer le @Transactional
    @Test
    @Transactional
    void getConnectionsById_shouldReturnCustomerConnectionList() {

        // Given a known username
        final Long customerId = 1L;
        final String username = "user";

        // When try to get connections // Then return a list of customer with this username
        Customer customer = customerRepository.findById(customerId)
                        .orElseThrow();
        assertThat(customer)
                .satisfies(user -> {
                    assertThat(user.getUsername()).isEqualTo(username);
                    assertThat(user.getConnections()).isNotNull();
                    assertThat(user.getConnections().size()).isEqualTo(2);
                } );

    }



    @Test
    void getConnectionsById_shouldThrowNotFoundException_whenIdNotFound() {

        // Given unknown Id
        final Long customerId = 22L;

        // When try to get connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.getConnectionsById(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

    }



    // FIXME: LazyInitializationException without @Transactional
    @Test
    void getTransactionsById_shouldReturnTransactionList() {

        // Given a known customer
        final Long customerId = 1L;
        final String username = "user";

        // When try to get transactions // Then return a list of transaction with this Id
        Customer customer = customerRepository.findById(customerId)
                        .orElseThrow();
        assertThat(customer)
                .satisfies(user -> {
                    assertThat(user.getUsername()).isEqualTo(username);
                    assertThat(user.getSentTransactions()).isNotNull();
                    assertThat(user.getSentTransactions().size()).isEqualTo(2);
                } );
    }




    @Test
    void getTransactionsById_shouldThrowNotFoundException_whenUsernameNotFound() {

        // Given an unknown customer
        final Long customerId = 22L;
        final String username = "unknownUsername";

        // When try to get connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.getTransactionsById(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

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
    void getEmailByUsername_shouldThrowNotFoundException_whenUsernameNotFound() {

        // Given an unknown username
        final String username = "unknownUsername";

        // When try to get connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.getEmailByUsername(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");
    }



    // FIXME: LazyInitializationException without @Transactional
    @Test
    @Transactional
    void addConnection_shouldAddConnection() {

        // Given a known username and email to add
        final Long customerId = 1L;
        final String username = "user";
        final String email = "copain@copain.com";

        // When try to add connection
        customerService.addConnection(customerId, email);

        // Then
        Customer customer = customerRepository.findById(customerId)
                        .orElseThrow();

        assertThat(customer)
                .satisfies(user -> {
                    assertThat(user.getUsername()).isEqualTo(username);
                    assertThat(user.getConnections()).isNotNull();
                    assertThat(user.getConnections())
                            .extracting("email")
                            .contains(email);
                });
    }



    @Test
    void addConnection_shouldThrowNotFoundException_whenIdNotFound() {

        // Given a known username and unknown email
        final Long customerId = 1L;
        final String email = "unknownCopain@copain.com";

        // When try to add connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.addConnection(customerId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");
    }



    @Test
    void addConnection_shouldThrowNotFoundException_whenEmailNotFound() {

        // Given a known id and unknown email
        final Long customerId = 1L;
        final String email = "unknownCopain@copain.com";

        // When try to add connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.addConnection(customerId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");
    }



    @Test
    void addConnection_shouldThrowConflictException_whenSelfUsername() {

        // Given a known id and email
        final Long customerId = 1L;
        final String email = "user@user.com";

        // When try to add connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.addConnection(customerId, email))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("yourself");
    }



    @Test
    void addConnection_shouldThrowConflictException_whenEmailAlreadyExistInList() {

        // Given a known username and email
        final Long customerId = 1L;
        final String email = "friend@friend.com";

        // When try to add connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.addConnection(customerId, email))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already");
    }

}