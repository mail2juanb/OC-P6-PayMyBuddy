package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
class CustomerServiceImplTest {

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void createCustomerTest() throws Exception{

        //GIVEN a new customer
        final String username = "John";
        final String email = "john.snow@test.test";
        final String rawPassword = "azerty123";


        // WHEN the user try to be registred
        customerService.createCustomer(username, email, rawPassword);


        // THEN
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

}