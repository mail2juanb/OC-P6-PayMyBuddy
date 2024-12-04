package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.dto.AddCustomerRequest;


public interface CustomerService {

    void createCustomer(String username, String email, String password);

}
