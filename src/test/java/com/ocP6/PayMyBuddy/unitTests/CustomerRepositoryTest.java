package com.ocP6.PayMyBuddy.unitTests;

import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO : Ca devrait pas être des tests avec Mockito et non intégration SpringBoot ?
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



    // TODO : Est ce utile d'écrire le test non passant : findById_shouldReturnEmpty
    @Test
    void findById_shouldReturnCustomer () {

        // Given an Id
        final Customer customer = new Customer();
        customer.setId(1L);
        customer.setUsername("user");

        // When try to find customer by id
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        Optional<Customer> result = customerRepository.findById(customer.getId());

        // Then return Customer
        assertTrue(result.isPresent());
        assertEquals("user", result.get().getUsername());
        verify(customerRepository).findById(customer.getId());
    }



    // TODO : A supprimer lorsque la méthode ne sera plus utilisée (ProfilController)
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


    // TODO : A supprimer lorsque la méthode ne sera plus utilisée (ProfilController)
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