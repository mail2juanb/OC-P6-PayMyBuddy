package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;

import java.math.BigDecimal;
import java.util.List;


public interface CustomerService {

    void createCustomer(String username, String email, String password);

    // TODO: Déplacer vers TransfertService
    List<Customer> getConnectionsById(Long userId);

    // TODO: Déplacer vers TransfertService
    List<Transaction> getTransactionsById(Long userId);

    String getEmailByUsername(String username);

    void addConnection(Long userId, String email);

    // TODO: Déplacer vers TransfertService
    BigDecimal getBalanceById(Long userId);

}
