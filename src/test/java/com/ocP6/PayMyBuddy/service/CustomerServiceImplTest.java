package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;




    @Test
    void createCustomer_shouldThrowConflictException_whenEmailAlreadyExists() throws Exception{

        // Given a customer already registred
        final String username = "user";
        final String email = "user@user.com";
        final String rawPassword = "user";

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(new Customer()));

        // When the user try to be registred // Then a Conflict Exception is thrown
        assertThatThrownBy(() -> customerService.createCustomer(username, email, rawPassword))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exist");
        verify(customerRepository, never()).save(any(Customer.class));

    }

}