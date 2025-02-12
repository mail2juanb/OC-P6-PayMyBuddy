package com.ocP6.PayMyBuddy;

import com.ocP6.PayMyBuddy.exception.*;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import com.ocP6.PayMyBuddy.service.CustomerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    void createCustomer_shouldSaveCustomer_whenEmailAndUsernameDoesNotExist () {

        // Given username, email and password
        final String username = "JohnDoe";
        final String email = "johndoe@example.com";
        final String password = "password";
        final String encodedPassword = "$2y$10$ogVEZam4sYSOGDpnMk81VeUKE.0OyKeE3mNeeRkaullSWtS0pzyXa";

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(customerRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // When try to create a customer
        customerService.createCustomer(username, email, password);

        // Then customer is created
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(1)).save(customerCaptor.capture());
        verify(passwordEncoder, times(1)).encode(password);

        Customer savedCustomer = customerCaptor.getValue();
        assertEquals(username, savedCustomer.getUsername());
        assertEquals(email, savedCustomer.getEmail());
        assertEquals(encodedPassword, savedCustomer.getPassword());
        assertEquals(BigDecimal.ZERO, savedCustomer.getBalance()); // Vérification du solde initial
        assertNotNull(savedCustomer.getSentTransactions());
        assertTrue(savedCustomer.getSentTransactions().isEmpty());
        assertNotNull(savedCustomer.getReceivedTransactions());
        assertTrue(savedCustomer.getReceivedTransactions().isEmpty());
        assertNotNull(savedCustomer.getConnections());
        assertTrue(savedCustomer.getConnections().isEmpty());

        verifyNoMoreInteractions(customerRepository, passwordEncoder);

    }



    @Test
    void createCustomer_shouldThrowAlreadyTakenUsernameException_whenUsernameAlreadyExists () {

        // Given
        final String username = "JohnDoe";
        final String email = "johndoe@example.com";
        final String password = "password";

        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(new Customer()));

        // When customer try to be registered // Then a AlreadyTakenUsernameException is thrown
        assertThrows(AlreadyTakenUsernameException.class, () -> customerService.createCustomer(username, email, password));
        verify(customerRepository, never()).save(any(Customer.class));

    }



    @Test
    void createCustomer_shouldThrowAlreadyTakenEmailException_whenEmailAlreadyExists () {

        // Given
        final String username = "JohnDoe";
        final String email = "johndoe@example.com";
        final String password = "password";

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(new Customer()));

        // When customer try to be registered // Then a AlreadyTakenEmailException is thrown
        assertThrows(AlreadyTakenEmailException.class, () -> customerService.createCustomer(username, email, password));
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
        assertThrows(NotFoundException.class, () -> customerService.getConnectionsById(id));
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

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, times(1)).findByEmailIgnoreCase(friend.getEmail());
        verify(customerRepository, times(1)).save(customer);
        verify(customerRepository, times(1)).save(friend);

        verifyNoMoreInteractions(customerRepository);

        assertEquals(1, customer.getConnections().size());
        assertEquals(1, friend.getConnections().size());

    }



    @Test
    void addConnection_shouldThrowNotFoundCustomerException_whenCustomerNotFound () {

        // Given
        final Long customerId = 1L;
        final String email = "friend@friend.com";

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When try to add connection // Then throw NotFoundCustomerException
        assertThrows(NotFoundCustomerException.class, () -> customerService.addConnection(customerId, email));

    }



    @Test
    void addConnection_shouldThrowNotFoundCustomerException_whenEmailNotFound () {

        // Given
        final Long customerId = 1L;
        final String email = "friend@friend.com";
        final Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // When try to addConnection // Then throw NotFoundException
        assertThrows(NotFoundCustomerException.class, () -> customerService.addConnection(customerId, email));

    }



    @Test
    void addConnection_shouldThrowConflictYourselfException_whenConnectingToSelf () {

        // Given
        final Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("self@self.com");

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmailIgnoreCase(customer.getEmail())).thenReturn(Optional.of(customer));

        // When try to add connection // Then throw ConflictException
        assertThrows(ConflictYourselfException.class, () -> customerService.addConnection(customer.getId(), customer.getEmail()));

    }



    @Test
    void addConnection_shouldThrowConflictConnectionException_whenAlreadyConnected () {

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
        assertThrows(ConflictConnectionException.class, () -> customerService.addConnection(customer.getId(), friend.getEmail()));

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

        verify(customerRepository, times(1)).findById(customer.getId());
        verifyNoMoreInteractions(customerRepository);

    }



    @Test
    void getBalanceById_shouldThrowNotFoundException_whenCustomerDoesNotExist () {

        // Given
        final Long userId = 2L;

        when(customerRepository.findById(userId)).thenReturn(Optional.empty());

        // When try to get balance of customer // Then throw NotFoundException
        assertThrows(NotFoundCustomerException.class, () -> customerService.getBalanceById(userId));

    }



    @Test
    void getUsernameById_shouldReturnUsername_whenCustomerExists() {

        // Given an id
        final Long userId = 1L;
        final Customer customer = new Customer();
        customer.setUsername("customer");

        when(customerRepository.findById(userId)).thenReturn(Optional.of(customer));

        // When try to get customer
        String result = customerService.getUsernameById(userId);

        // Then username returned
        assertEquals("customer", result);
        verify(customerRepository, times(1)).findById(userId);

    }



    @Test
    void getUsernameById_shouldThrowNotFoundCustomerException_whenCustomerNotFound() {

        // Given an id
        final Long userId = 1L;

        when(customerRepository.findById(userId)).thenReturn(Optional.empty());

        // When try to get customer // Then throw NotFoundCustomerException
        assertThrows(NotFoundCustomerException.class, () -> customerService.getUsernameById(userId));
        verify(customerRepository, times(1)).findById(userId);

    }



    @Test
    void getEmailById_ShouldReturnEmail_WhenCustomerExists() {

        // Given an id
        final Long userId = 1L;
        final String email = "test@example.com";
        final Customer customer = new Customer();
        customer.setEmail(email);

        when(customerRepository.findById(userId)).thenReturn(Optional.of(customer));

        // When try to get customer
        String result = customerService.getEmailById(userId);

        // Then return what expected
        assertEquals(email, result);
        verify(customerRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(customerRepository);

    }



    @Test
    void getEmailById_shouldThrowNotFoundCustomerException_whenCustomerNotFound() {

        // Given an id
        Long userId = 1L;

        when(customerRepository.findById(userId)).thenReturn(Optional.empty());

        // When try to get customer // Then throw NotFoundCustomerException
        assertThrows(NotFoundCustomerException.class, () -> customerService.getEmailById(userId));
        verify(customerRepository, times(1)).findById(userId);

    }



    @Test
    void updateCustomer_shouldUpdateCustomer_whenValidRequest() {

        // Given a userId and new : username, email and password
        final Long userId = 1L;
        final String newUsername = "newUsername";
        final String newEmail = "new@new.com";
        final String newPassword = "newPassword";
        final String encodedPassword = "encodedPassword";

        final Customer existingCustomer = new Customer();
        existingCustomer.setId(userId);
        existingCustomer.setUsername("oldUsername");
        existingCustomer.setEmail("old@old.com");
        existingCustomer.setPassword("oldPassword");

        when(customerRepository.findById(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByUsername(newUsername)).thenReturn(Optional.empty());
        when(customerRepository.findByEmailIgnoreCase(newEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // When try to update customer
        customerService.updateCustomer(userId, newUsername, newEmail, newPassword);

        // Then customer is updated
        assertEquals(newUsername, existingCustomer.getUsername());
        assertEquals(newEmail, existingCustomer.getEmail());
        assertEquals(encodedPassword, existingCustomer.getPassword());

        verify(customerRepository, times(1)).findById(userId);
        verify(customerRepository, times(1)).save(existingCustomer);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verifyNoMoreInteractions(customerRepository, passwordEncoder);

    }




    @Test
    void updateCustomer_shouldThrowNotFoundCustomerException_whenCustomerNotFound() {

        // Given userId
        final Long userId = 1L;

        when(customerRepository.findById(userId)).thenReturn(Optional.empty());

        // When try to update customer // Then throw NotFoundCustomerException
        assertThrows(NotFoundCustomerException.class, () -> customerService.updateCustomer(userId, "newUsername", "new@example.com", "newPassword"));
        verify(customerRepository, never()).save(any());

    }



    @Test
    void updateCustomer_shouldThrowAlreadyTakenUsernameException_whenUsernameAlreadyTaken() {

        // Given userId and  new username already taken
        final Long userId = 1L;
        final String newUsername = "takenUsername";

        final Customer existingCustomer = new Customer();
        existingCustomer.setId(userId);
        existingCustomer.setUsername("oldUsername");
        existingCustomer.setEmail("old@example.com");

        final Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setUsername(newUsername);

        when(customerRepository.findById(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByUsername(newUsername)).thenReturn(Optional.of(anotherCustomer));

        // When try to update customer // Then throw AlreadyTakenUsernameException
        assertThrows(AlreadyTakenUsernameException.class, () -> customerService.updateCustomer(userId, newUsername, "new@example.com", "newPassword"));
        verify(customerRepository, never()).save(any());

    }



    @Test
    void updateCustomer_shouldThrowAlreadyTakenEmailException_whenEmailAlreadyTaken() {
        // Given userId and new email already taken
        final Long userId = 1L;
        final String newEmail = "taken@example.com";

        final Customer existingCustomer = new Customer();
        existingCustomer.setId(userId);
        existingCustomer.setUsername("oldUsername");
        existingCustomer.setEmail("old@example.com");

        final Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setEmail(newEmail);

        when(customerRepository.findById(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByEmailIgnoreCase(newEmail)).thenReturn(Optional.of(anotherCustomer));

        // When try to update customer // Then throw AlreadyTakenEmailException
        assertThrows(AlreadyTakenEmailException.class, () -> customerService.updateCustomer(userId, "newUsername", newEmail, "newPassword"));
        verify(customerRepository, never()).save(any());

    }



    @Test
    void updateCustomer_shouldThrowAlreadyTakenEmailException_whenOnlyEmailIsAlreadyTaken() {
        // Given userId and new email already taken, but username remains the same
        final Long userId = 1L;
        final String sameUsername = "existingUser";
        final String takenEmail = "taken@example.com";
        final String newPassword = "newPassword";

        final Customer existingCustomer = new Customer();
        existingCustomer.setId(userId);
        existingCustomer.setUsername(sameUsername);
        existingCustomer.setEmail("old@example.com");

        final Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setEmail(takenEmail);

        when(customerRepository.findById(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByUsername(sameUsername)).thenReturn(Optional.of(existingCustomer)); // Même utilisateur
        when(customerRepository.findByEmailIgnoreCase(takenEmail)).thenReturn(Optional.of(anotherCustomer));

        // When trying to update customer // Then should throw AlreadyTakenEmailException
        assertThrows(AlreadyTakenEmailException.class, () -> customerService.updateCustomer(userId, sameUsername, takenEmail, newPassword));
        verify(customerRepository, never()).save(any());

    }



    @Test
    void updateCustomer_shouldThrowAlreadyTakenUsernameException_whenOnlyUsernameIsAlreadyTaken() {
        // Given userId and new username already taken, but email remains the same
        final Long userId = 1L;
        final String takenUsername = "takenUsername";
        final String sameEmail = "existing@example.com";
        final String newPassword = "newPassword";

        final Customer existingCustomer = new Customer();
        existingCustomer.setId(userId);
        existingCustomer.setUsername("oldUsername");
        existingCustomer.setEmail(sameEmail);

        final Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setUsername(takenUsername);

        when(customerRepository.findById(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByUsername(takenUsername)).thenReturn(Optional.of(anotherCustomer));

        // When trying to update customer // Then should throw AlreadyTakenUsernameException
        assertThrows(AlreadyTakenUsernameException.class, () -> customerService.updateCustomer(userId, takenUsername, sameEmail, newPassword));
        verify(customerRepository, never()).save(any());

    }



    @Test
    void updateCustomer_shouldUpdatePasswordOnly_whenUsernameAndEmailRemainTheSame() {
        // Given userId and unchanged username and email, but new password
        final Long userId = 1L;
        final String sameUsername = "sameUsername";
        final String sameEmail = "same@example.com";
        final String newPassword = "newPassword";
        final String encodedPassword = "encodedNewPassword";

        final Customer existingCustomer = new Customer();
        existingCustomer.setId(userId);
        existingCustomer.setUsername(sameUsername);
        existingCustomer.setEmail(sameEmail);
        existingCustomer.setPassword("oldPassword");

        when(customerRepository.findById(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByUsername(sameUsername)).thenReturn(Optional.of(existingCustomer)); // Même utilisateur
        when(customerRepository.findByEmailIgnoreCase(sameEmail)).thenReturn(Optional.of(existingCustomer)); // Même utilisateur
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // When updating customer
        customerService.updateCustomer(userId, sameUsername, sameEmail, newPassword);

        // Then password should be updated but username and email should remain unchanged
        assertEquals(sameUsername, existingCustomer.getUsername());
        assertEquals(sameEmail, existingCustomer.getEmail());
        assertEquals(encodedPassword, existingCustomer.getPassword());

        verify(customerRepository, times(1)).save(existingCustomer);
        verify(passwordEncoder, times(1)).encode(newPassword);

    }



    @Test
    void creditBalance_shouldIncreaseBalance_whenUserExistsAndAmountIsPositive() {

        // Given a user with an initial balance exists in the repository
        final Long userId = 1L;
        final BigDecimal initialBalance = new BigDecimal("100.00");
        final BigDecimal amountToCredit = new BigDecimal("50.00");
        final Customer customer = new Customer();
        customer.setId(userId);
        customer.setBalance(initialBalance);

        when(customerRepository.findById(userId)).thenReturn(Optional.of(customer));

        // When creditBalance method is called with a positive amount
        customerService.creditBalance(userId, amountToCredit);

        // Then the customer balance should be increased by the credited amount
        assertEquals(new BigDecimal("150.00"), customer.getBalance());
        verify(customerRepository, times(1)).save(customer);

    }



    @Test
    void creditBalance_shouldThrowNotFoundCustomerException_whenUserDoesNotExist() {

        // Given no user exists with the specified ID
        final Long userId = 1L;
        final BigDecimal amountToCredit = new BigDecimal("50.00");

        when(customerRepository.findById(userId)).thenReturn(Optional.empty());

        // When the creditBalance method is called for a non-existent user

        // Then a NotFoundCustomerException should be thrown
        assertThrows(NotFoundCustomerException.class, () -> customerService.creditBalance(userId, amountToCredit));
        verify(customerRepository, never()).save(any(Customer.class));

    }



    @Test
    void creditBalance_shouldThrowIllegalArgumentException_whenAmountIsZeroOrNegative() {

        // Given a user exists in the repository
        final Long userId = 1L;
        final BigDecimal invalidAmount = new BigDecimal("0.00");
        final Customer customer = new Customer();
        customer.setId(userId);
        customer.setBalance(new BigDecimal("100.00"));

        when(customerRepository.findById(userId)).thenReturn(Optional.of(customer));

        // When the creditBalance method is called with a zero or negative amount

        // Then an IllegalArgumentException should be thrown
        assertThrows(IllegalArgumentException.class, () -> customerService.creditBalance(userId, invalidAmount));
        verify(customerRepository, never()).save(any(Customer.class));

    }


}