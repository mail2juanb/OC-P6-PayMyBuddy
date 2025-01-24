package com.ocP6.PayMyBuddy.unitTests;

import com.ocP6.PayMyBuddy.exception.*;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import com.ocP6.PayMyBuddy.repository.TransactionRepository;
import com.ocP6.PayMyBuddy.service.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {


    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;




    // TODO : Ecrire les tests unitaires pour la méthode : List<Transaction> getTransactionsById(Long userId)


    @Test
    public void createTransfert_shouldSucceed_whenValidInput () {

        // Given a Customer and a Relation
        final Long customerId = 98L;
        final Long relationId = 99L;
        final String description = "Payment for services";
        final BigDecimal amount = new BigDecimal("50.00");

        Customer relation = new Customer();
        relation.setId(relationId);
        relation.setBalance(new BigDecimal("20.00"));

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setBalance(new BigDecimal("100.00"));
        customer.setConnections(List.of(relation));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(relationId)).thenReturn(Optional.of(relation));

        // When try to create transfert
        transactionService.createTransfert(customerId, relationId, description, amount);

        // Then
        assertEquals(new BigDecimal("50.00"), customer.getBalance()); // 100 - 50
        assertEquals(new BigDecimal("70.00"), relation.getBalance()); // 20 + 50
        verify(transactionRepository).save(any(Transaction.class));
        verify(customerRepository, times(2)).save(any(Customer.class));

    }



    @Test
    void createTransfert_shouldThrowNotFoundCustomerException_whenCustomerNotFound () {
        // Given customer and relation id
        final Long customerId = 98L;
        final Long relationId = 99L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When try to create transfert // Then throw NotFoundCustomerException
        assertThrows(NotFoundCustomerException.class, () -> transactionService.createTransfert(customerId, relationId, "desc", BigDecimal.TEN));

    }



    @Test
    void createTransfert_shouldThrowNotFoundRelationException_whenRelationNotFound () {
        // Given customer and relation id
        final Long customerId = 98L;
        final Long relationId = 99L;
        final Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(relationId)).thenReturn(Optional.empty());

        // When try to create transfert // Then throw NotFoundRelationException
        assertThrows(NotFoundRelationException.class, () -> transactionService.createTransfert(customerId, relationId, "desc", BigDecimal.TEN));
    }



    @Test
    void createTransfert_shouldThrowConflictYourselfException_whenSameCustomerAndRelation () {
        // Given customer id
        final Long customerId = 1L;
        final Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When try to create transfert // Then throw ConflictYourselfException
        assertThrows(ConflictYourselfException.class, () -> transactionService.createTransfert(customerId, customerId, "desc", BigDecimal.TEN));
    }



    @Test
    void createTransfert_shouldThrowConflictConnectionException_whenNoConnectionsList () {
        // Given customer and relation ids
        final Long customerId = 98L;
        final Long relationId = 99L;
        final Customer customer = new Customer();
        customer.setId(customerId);
        customer.setConnections(Collections.emptyList()); // Pas de connexions
        final Customer relation = new Customer();
        relation.setId(relationId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(relationId)).thenReturn(Optional.of(relation));

        // When try to create transfert // Then throw ConflictConnectionException
        assertThrows(ConflictConnectionException.class, () -> transactionService.createTransfert(customerId, relationId, "desc", BigDecimal.TEN));
    }



    @Test
    void createTransfert_shouldThrowConflictConnectionException_whenRelationNotInConnectionsList () {
        // Given customer and relation ids
        final Long customerId = 98L;
        final Long relationId = 99L;
        final Customer relation = new Customer();
        relation.setId(relationId);
        final Customer customer = new Customer();
        customer.setId(customerId);
        customer.setConnections(List.of(new Customer("otherFriend", "other@other.com", "otherFriend"))); // Relation n'est pas dans les connexions

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(relationId)).thenReturn(Optional.of(relation));

        // When try to create transfert // Then throw ConflictConnectionException
        assertThrows(ConflictConnectionException.class, () -> transactionService.createTransfert(customerId, relationId, "desc", BigDecimal.TEN));
    }



    @Test
    void createTransfert_shouldThrowConflictExceedsException_whenBalanceIsInsufficient() {
        // Given customer and relation ids
        final Long customerId = 98L;
        final Long relationId = 99L;
        final BigDecimal amount = new BigDecimal("100.00");

        final Customer relation = new Customer();
        relation.setId(relationId);
        relation.setBalance(new BigDecimal("20.00"));

        final Customer customer = new Customer();
        customer.setId(customerId);
        customer.setBalance(new BigDecimal("50.00")); // Solde insuffisant
        customer.setConnections(List.of(relation));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(relationId)).thenReturn(Optional.of(relation));

        // When try to create transfert // Then throw ConflictExceedsException
        assertThrows(ConflictExceedsException.class, () -> transactionService.createTransfert(customerId, relationId, "desc", amount));
    }

}
