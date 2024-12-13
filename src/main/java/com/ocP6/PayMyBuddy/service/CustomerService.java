package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;

import java.util.List;


public interface CustomerService {

    void createCustomer(String username, String email, String password);

    List<Customer> getConnectionsByUsername(String username);

    List<Transaction> getTransactionsByUsername(String username);

    String getEmailByUsername(String username);

    void addConnection(String username, String email);

}
