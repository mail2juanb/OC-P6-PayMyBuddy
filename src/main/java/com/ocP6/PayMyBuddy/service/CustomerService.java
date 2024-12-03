package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Customer;

import java.util.List;

public interface CustomerService {

    String findUsernameById(Long id);

    long countUsers();

    List<Customer> findAll();

    List<Customer> getConnectionsByUserId(Long userId);

    List<Customer> getConnectionsByUserIdMethodB(Long userId);

    void save(Customer customer);

}
