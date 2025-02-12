package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Customer;

import java.math.BigDecimal;
import java.util.List;


public interface CustomerService {

    void createCustomer(String username, String email, String password);

    List<Customer> getConnectionsById(Long userId);

    void addConnection(Long userId, String email);

    BigDecimal getBalanceById(Long userId);

    String getUsernameById(Long userId);

    String getEmailById(Long userId);

    void updateCustomer(Long userId, String username, String email, String password);

    void creditBalance(Long userId, BigDecimal amount);

}
