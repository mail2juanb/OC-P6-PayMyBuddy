package com.ocP6.PayMyBuddy.unitTests;

import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;



@Slf4j
@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private final Customer customer = new Customer();



    @BeforeEach
    public void setUp () {
        customer.setUsername("testUser");
        customer.setEmail("testUser@user.com");
        customer.setPassword("testUser");

        customerRepository.save(customer);

    }


    @AfterEach
    public void tearDown() {
        // Ne supprime le customer que si l'ID est défini
        if (customer.getId() != null) {
            customerRepository.delete(customer);
        }
    }



    @Test
    void save_shouldSaveCustomer () {

        // Given a Customer by @BeforeEach
        // When try to save Customer // Then Customer is saved
        assertTrue(customerRepository.findById(customer.getId()).isPresent());

    }



    @Test
    void save_shouldNotSaveCustomer_whenCustomerAlreadyExists () {

        // Given a new Customer
        final Customer newCustomer = new Customer();
        newCustomer.setEmail(customer.getEmail());
        newCustomer.setUsername(customer.getUsername());
        newCustomer.setPassword(customer.getPassword());

        // When try to save Customer // Then Customer is not saved because already exists
        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.save(newCustomer));

    }



    @ParameterizedTest
    @MethodSource("provideInvalidCustomer")
    void save_shouldNotSaveCustomer_whenInvalideCustomer (Customer customer) {

        // Given new Customer by provideInvalidCustomer
        // When try to save Customer // Then Customer is not saved because already exists
        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.save(customer));

    }

    // Returns Invalide Customer
    static Stream<Customer> provideInvalidCustomer () {

        final Customer customer1 = new Customer();
        customer1.setUsername(null);
        customer1.setEmail("customer@customer.com");
        customer1.setPassword("customer");

        final Customer customer2 = new Customer();
        customer2.setUsername("customer2");
        customer2.setEmail(null);
        customer2.setPassword("customer");

        final Customer customer3 = new Customer();
        customer3.setUsername("customer3");
        customer3.setEmail("customer@customer.com");
        customer3.setPassword(null);

        final Customer customer4 = new Customer();
        customer4.setUsername("customer3");
        customer4.setEmail("customer@customer.com");
        customer4.setPassword("customer");
        customer4.setBalance(null);

        return Stream.of(customer1, customer2, customer3, customer4);

    }



    @ParameterizedTest
    @MethodSource("provideValidEmail")
    void findByEmailIgnoreCase_shouldReturnCustomer (String email) {

        // Given a known email by provideValidEmail

        // When try to find customer by email
        Optional<Customer> result = customerRepository.findByEmailIgnoreCase(email);

        // Then return Customer
        assertTrue(result.isPresent());
        assertEquals(customer.getId(), result.get().getId());
        assertEquals(customer.getEmail(), result.get().getEmail());
        assertEquals(customer.getUsername(), result.get().getUsername());
        assertEquals(customer.getPassword(), result.get().getPassword());

    }

    // Returns Valid email
    static Stream<String> provideValidEmail () {
        String email1 = "testUser@user.com";
        String email2 = "TESTUSER@user.com";
        String email3 = "testUser@User.com";

        return Stream.of(email1, email2, email3);

    }


    
    @Test
    void findByEmailIgnoreCase_shouldReturnEmpty_whenEmailNotFound () {

        // Given an unknown email
        String email = "unknown@user.com";

        // When try to find customer by email
        Optional<Customer> result = customerRepository.findByEmailIgnoreCase(email);

        // Then return Empty
        assertTrue(result.isEmpty());

    }



    @ParameterizedTest
    @MethodSource("provideInvalidEmail")
    void findByEmailIgnoreCase_shouldReturnEmpty_whenEmailNotValid (String email) {

        // Given a known email by provideInvalidEmail

        // When try to find customer by email
        Optional<Customer> result = customerRepository.findByEmailIgnoreCase(email);

        // Then return Empty
       assertTrue(result.isEmpty());

    }

    // Returns invalid email
    static Stream<String> provideInvalidEmail() {
        String email1 = "user.com";
        String email2 = "User@user";
        String email3 = "userUsercom";

        return Stream.of(email1, email2, email3);

    }



    @Test
    void findById_shouldReturnCustomer () {

        // Given an id
        final Long id = customer.getId();

        // When try to find customer by id
        Optional<Customer> result = customerRepository.findById(id);

        // Then return Customer
        assertTrue(result.isPresent());
        assertEquals(customer.getUsername(), result.get().getUsername());

    }



    @Test
    void findById_shouldReturnEmpty_whenCustomerNotFound () {

        // Given an id
        final Long id = 99L;

        // When try to find customer by id
        Optional<Customer> result = customerRepository.findById(id);

        // Then return Customer
        assertTrue(result.isEmpty());

    }



    @Test
    void findByUsername_shouldReturnCustomer () {
        // Given a username
        final String username = customer.getUsername();

        // When try to find by username in repository
        Optional<Customer> result = customerRepository.findByUsername(username);

        // Then customer is found
        assertThat(result).isPresent();
        assertThat(result.map(Customer::getUsername)).contains(customer.getUsername());

    }



    @Test
    void findByUsername_shouldReturnEmpty_whenUsernameNotExists () {
        // Given a username
        final String username = "unknownUsername";

        // When try to find by username in repository
        Optional<Customer> result = customerRepository.findByUsername(username);

        // Then customer is empty
        assertThat(result).isEmpty();

    }

}