package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;




    @Test
    void getConnectionsByUsername_shouldThrowNotFoundException_whenUsernameNotFound() {

        // Given unknown username
        final String username = "unknownUsername";

        // When try to get connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.getConnectionsByUsername(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

    }



    @Test
    void getTransactionsByUsername_shouldThrowNotFoundException_whenUsernameNotFound() {

        // Given an unknown username
        final String username = "unknownUsername";

        // When try to get connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.getTransactionsByUsername(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

    }


    @Test
    void getEmailByUsername_shouldThrowNotFoundException_whenUsernameNotFound() {

        // Given an unknown username
        final String username = "unknownUsername";

        // When try to get connections // Then NotFoundException is thrown
        assertThatThrownBy(() -> customerService.getTransactionsByUsername(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");
    }



}