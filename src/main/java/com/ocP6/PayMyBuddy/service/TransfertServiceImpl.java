package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
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
public class TransfertServiceImpl implements TransfertService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public void createTransfert(Long customerId, Long relationId, String description, BigDecimal amount) {

        //TODO: Vérifier que le userId et que le relationId existent.
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("customer not found -> " + customerId));
        Customer relation = customerRepository.findById(relationId)
                .orElseThrow(() -> new NotFoundException("relation not found -> " + relationId));

        // NOTE: Vérifie que ce n'est pas le même customer.
        if (relationId.equals(customerId)) {
            throw new ConflictException("You can't be connected to yourself");
        }

        //TODO: Vérifier qu'ils sont bien amis
        List<Customer> customerConnections = customer.getConnections();
        if (customerConnections.isEmpty()) {
            throw new ConflictException("customer connection list is empty.");
        }

        //TODO: Il faut vérifier que relation est bien dans la liste du customer
        boolean isConnected = customerConnections.stream()
                .anyMatch(connection -> Objects.equals(connection.getId(), relationId));
        if (!isConnected) {
            throw new ConflictException("You are not connected to this customer.");
        }

        //TODO: Vérifier que le montant (amount) à transférer est bien inférieur à son solde (balance)
        BigDecimal balance = customer.getBalance();
        if (balance.compareTo(amount) < 0) {
            throw new ConflictException("Le montant à transférer dépasse votre solde disponible.");
        }

        //TODO: Sauvegarde de la transaction
        final Transaction transaction = new Transaction(customer, relation, description, amount);
        transactionRepository.save(transaction);

        //TODO: Mise à jour de la balance du customer
        final BigDecimal newCustomerBalance = balance.subtract(amount);
        customer.setBalance(newCustomerBalance);
        customerRepository.save(customer);

        //TODO: Mise à jour de la balance du receiver
        BigDecimal relationBalance = relation.getBalance();
        final BigDecimal newRelationBalance = relationBalance.add(amount);
        relation.setBalance(newRelationBalance);
        customerRepository.save(relation);



    }

}
