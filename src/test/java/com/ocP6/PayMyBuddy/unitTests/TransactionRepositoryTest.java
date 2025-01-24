package com.ocP6.PayMyBuddy.unitTests;

import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import com.ocP6.PayMyBuddy.repository.TransactionRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;


    private final Customer sender = new Customer();
    private final Customer receiver = new Customer();



    @BeforeEach
    public void setUp () {

        sender.setUsername("Alice");
        sender.setEmail("alice@mail.com");
        sender.setPassword("password123");
        customerRepository.save(sender);


        receiver.setUsername("Bob");
        receiver.setEmail("bob@mail.com");
        receiver.setPassword("password456");
        customerRepository.save(receiver);

    }


    

    @Test
    void save_shouldSaveTransaction () {

        // Given a Transaction
        final Transaction transaction = new Transaction(sender, receiver, "Description Exemple", BigDecimal.valueOf(25.50));

        // When try to save Transaction
        transactionRepository.save(transaction);

        // Then Transaction is saved
        Optional<Transaction> result = transactionRepository.findById(transaction.getId());
        assertTrue(result.isPresent());
        assertEquals(transaction.getAmount(), result.get().getAmount());
        assertEquals(transaction.getDescription(), result.get().getDescription());
        assertEquals(transaction.getSender().getUsername(), result.get().getSender().getUsername());
        assertEquals(transaction.getReceiver().getUsername(), result.get().getReceiver().getUsername());

    }



    @ParameterizedTest
    @MethodSource("provideInvalidAmount")
    void save_shouldThrowConstraintViolationException_whenAmountIsNotValid (BigDecimal amount) {

        // Given an invalid transaction by provideInvalidAmount
        final Transaction transaction = new Transaction(sender, receiver, "Description Exemple", amount);

        // When try to save transaction // Then throw ConstraintViolationException
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> transactionRepository.save(transaction));
        assertTrue(exception.getMessage().contains("Amount must be greater than 0"),
                "Expected error message to contain: 'Amount must be greater than 0', but was: " + exception.getMessage()
        );

    }

    // Returns Invalide Amount
    static Stream<BigDecimal> provideInvalidAmount () {

        final BigDecimal amount0 = BigDecimal.ZERO;
        final BigDecimal amount1 = BigDecimal.valueOf(-1);

        return Stream.of(amount0, amount1);

    }



    @Test
    void save_shouldThrowDataIntegrityViolationException_whenAmountIsNull () {

        // Given an invalid transaction with null amount
        final Transaction transaction = new Transaction(sender, receiver, "Description Exemple", null);

        // When try to save transaction // Then throw DataIntegrityViolationException
        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(transaction));

    }



    @Test
    void save_shouldThrowDataIntegrityViolationException_whenSenderIsNull () {

        // Given an invalid transaction with sender is null
        final Transaction transaction = new Transaction(null, receiver, "No sender", BigDecimal.valueOf(34.43));

        // When try to save transaction // Then throw XXX
        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(transaction));

    }



    @Test
    void save_shouldThrowDataIntegrityViolationException_whenReceiverIsNull () {

        // Given an invalid transaction with receiver is null
        final Transaction transaction = new Transaction(sender, null, "No receiver", BigDecimal.valueOf(25.52));

        // When try to save transaction // Then throw XXX
        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(transaction));

    }

}