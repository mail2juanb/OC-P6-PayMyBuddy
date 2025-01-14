package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Customer;

import java.math.BigDecimal;
import java.util.List;


public interface CustomerService {

    void createCustomer(String username, String email, String password);

    List<Customer> getConnectionsById(Long userId);

    String getEmailByUsername(String username);

    void addConnection(Long userId, String email);

    BigDecimal getBalanceById(Long userId);

}
