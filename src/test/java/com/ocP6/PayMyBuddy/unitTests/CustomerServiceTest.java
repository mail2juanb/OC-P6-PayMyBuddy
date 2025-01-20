package com.ocP6.PayMyBuddy.unitTests;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import com.ocP6.PayMyBuddy.service.CustomerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;




    @Test
    void createCustomer_shouldSaveCustomer_whenEmailDoesNotExist () {

        // Given
        final String username = "JohnDoe";
        final String email = "johndoe@example.com";
        final String password = "password";
        final String encodedPassword = "$2y$10$ogVEZam4sYSOGDpnMk81VeUKE.0OyKeE3mNeeRkaullSWtS0pzyXa";

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // When try to create a customer
        customerService.createCustomer(username, email, password);

        // Then customer is created
        verify(customerRepository).save(argThat(customer ->
                customer.getUsername().equals(username) &&
                customer.getEmail().equals(email) &&
                customer.getPassword().equals(encodedPassword))
        );

    }



    @Test
    void createCustomer_shouldThrowConflictException_whenEmailAlreadyExists () {

        // Given
        final String username = "JohnDoe";
        final String email = "johndoe@example.com";
        final String password = "password";

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(new Customer()));

        // When customer try to be registred // Then a Conflict Exception is thrown
        ConflictException exception = assertThrows(ConflictException.class, () ->
                customerService.createCustomer(username, email, password)
                );
        assertTrue(exception.getMessage().contains("already"));
        verify(customerRepository, never()).save(any(Customer.class));

    }



    @Test
    void getConnectionsById_shouldReturnConnections_whenCustomerExists () {

        // Given
        final Long id = 99L;
        final Customer customer = new Customer();
        final Customer connection1 = new Customer();
        final Customer connection2 = new Customer();
        customer.setConnections(List.of(connection1, connection2));

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // When try to get connections
        List<Customer> result = customerService.getConnectionsById(id);

        // Then return list of Customer connected to customer id
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(connection1));
        assertTrue(result.contains(connection2));
        verify(customerRepository, times(1)).findById(id);

    }



    @Test
    void getConnectionsById_shouldThrowNotFoundException_whenCustomerNotExists () {

        // Given
        final Long id = 99L;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // When try to get connections // Then throw a NotFoundException
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            customerService.getConnectionsById(id);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(customerRepository, times(1)).findById(id);

    }



    @Test
    void getConnectionsById_shouldReturnEmptyList_whenCustomerHasNoConnectionsYet () {

        // Given
        final Long id = 99L;
        final Customer customer = new Customer();
        customer.setConnections(Collections.emptyList());

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // When try to get connections
        List<Customer> result = customerService.getConnectionsById(id);

        // Then return an empty list of Customer connected to customer id
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findById(id);

    }



    @Test
    void addConnection_shouldAddConnection () {

        // Given
        final Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("example@example.com");
        customer.setConnections(new ArrayList<>());

        final Customer friend = new Customer();
        friend.setId(2L);
        friend.setEmail("friend@friend.com");
        friend.setConnections(new ArrayList<>());

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmailIgnoreCase(friend.getEmail())).thenReturn(Optional.of(friend));

        // When try to add connection
        customerService.addConnection(customer.getId(), friend.getEmail());

        // Then friend added to customer's connection
        assertTrue(customer.getConnections().contains(friend));
        assertTrue(friend.getConnections().contains(customer));

        verify(customerRepository, times(1)).save(customer);
        verify(customerRepository, times(1)).save(friend);

    }



    @Test
    void addConnection_shouldThrowNotFoundException_whenCustomerNotFound () {

        // Given
        final Long customerId = 1L;
        final String email = "friend@friend.com";

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When try to add connection // Then throw NotFoundException
        assertThrows(NotFoundException.class, () -> customerService.addConnection(customerId, email));

    }



    @Test
    void addConnection_shouldThrowNotFoundException_whenEmailNotFound () {

        // Given
        final Long customerId = 1L;
        final String email = "friend@friend.com";
        final Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // When try to addConnection // Then throw NotFoundException
        assertThrows(NotFoundException.class, () -> customerService.addConnection(customerId, email));

    }



    @Test
    void addConnection_shouldThrowConflictException_whenConnectingToSelf () {

        // Given
        final Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("self@self.com");

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmailIgnoreCase(customer.getEmail())).thenReturn(Optional.of(customer));

        // When try to add connection // Then throw ConflictException
        assertThrows(ConflictException.class, () -> customerService.addConnection(customer.getId(), customer.getEmail()));

    }



    @Test
    void addConnection_shouldThrowConflictException_whenAlreadyConnected () {

        // Given
        final Customer customer = new Customer();
        customer.setId(1L);
        customer.setConnections(new ArrayList<>());

        final Customer friend = new Customer();
        friend.setId(2L);
        friend.setEmail("friend@friend.com");
        friend.setConnections(new ArrayList<>());

        customer.getConnections().add(friend);
        friend.getConnections().add(customer);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmailIgnoreCase(friend.getEmail())).thenReturn(Optional.of(friend));

        // When try to add connection // Then throw ConflictException
        assertThrows(ConflictException.class, () -> customerService.addConnection(customer.getId(), friend.getEmail()));

    }



    @Test
    void getBalanceById_shouldReturnBalance_whenCustomerExists () {

        // Given
        final BigDecimal expectedBalance = new BigDecimal("100.50");
        final Customer customer = new Customer();
        customer.setId(1L);
        customer.setBalance(expectedBalance);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        // When try to get balance of customer
        BigDecimal result = customerService.getBalanceById(customer.getId());

        // Then return balance of customer
        assertNotNull(result);
        assertEquals(expectedBalance, result);

    }



    @Test
    void getBalanceById_shouldThrowNotFoundException_whenCustomerDoesNotExist () {

        // Given
        final Long userId = 2L;

        when(customerRepository.findById(userId)).thenReturn(Optional.empty());

        // When try to get balance of customer // Then throw NotFoundException
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            customerService.getBalanceById(userId);
        });
        assertTrue(exception.getMessage().contains("not found"));
    }



}