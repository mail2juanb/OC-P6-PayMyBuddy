package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;

import java.util.List;


public interface CustomerService {

    void createCustomer(String username, String email, String password);

    List<Customer> getConnectionsById(Long userId);

    List<Transaction> getTransactionsById(Long userId);

    String getEmailByUsername(String username);

    void addConnection(Long userId, String email);

}
