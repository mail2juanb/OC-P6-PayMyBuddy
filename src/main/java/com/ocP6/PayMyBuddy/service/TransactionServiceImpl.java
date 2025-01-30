package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.*;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import com.ocP6.PayMyBuddy.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;



    public List<Transaction> getTransactionsById(Long userId) {

        return customerRepository.findById(userId)
                .map(Customer::getSentTransactions)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + userId));

    }



    public void createTransfert(Long customerId, Long relationId, String description, BigDecimal amount) {

        //NOTE: Vérifier que le userId et que le relationId existent.
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + customerId));
        Customer relation = customerRepository.findById(relationId)
                .orElseThrow(() -> new NotFoundRelationException("L'utilisateur est introuvable avec cet ID : " + customerId));

        // NOTE: Vérifie que ce n'est pas le même customer.
        if (relationId.equals(customerId)) {
            throw new ConflictYourselfException("Vous ne pouvez être en relation avec vous même !");
        }

        //NOTE: Vérifier qu'ils sont bien amis
        List<Customer> customerConnections = customer.getConnections();
        if (customerConnections.isEmpty()) {
            throw new ConflictConnectionException("Vous n'avez aucune relation enregistrée.");
        }

        //NOTE: Il faut vérifier que relation est bien dans la liste du customer
        boolean isConnected = customerConnections.stream()
                .anyMatch(connection -> Objects.equals(connection.getId(), relationId));
        if (!isConnected) {
            throw new ConflictConnectionException("Vous n'êtes pas en relation avec cet utilisateur.");
        }

        //NOTE: Vérifier que le montant (amount) à transférer est bien inférieur à son solde (balance)
        BigDecimal balance = customer.getBalance();
        if (balance.compareTo(amount) < 0) {
            throw new ConflictExceedsException("Le montant à transférer est supérieur à votre solde disponible.");
        }

        //NOTE: Sauvegarde de la transaction
        final Transaction transaction = new Transaction(customer, relation, description, amount);
        transactionRepository.save(transaction);

        //NOTE: Mise à jour de la balance du customer
        final BigDecimal newCustomerBalance = balance.subtract(amount);
        customer.setBalance(newCustomerBalance);
        customerRepository.save(customer);

        //NOTE: Mise à jour de la balance du receiver
        BigDecimal relationBalance = relation.getBalance();
        final BigDecimal newRelationBalance = relationBalance.add(amount);
        relation.setBalance(newRelationBalance);
        customerRepository.save(relation);

    }

}
