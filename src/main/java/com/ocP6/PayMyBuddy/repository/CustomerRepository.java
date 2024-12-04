package com.ocP6.PayMyBuddy.repository;

import com.ocP6.PayMyBuddy.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmailIgnoreCase(String email);

}
