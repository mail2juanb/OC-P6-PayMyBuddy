package com.ocP6.PayMyBuddy.repository;

import com.ocP6.PayMyBuddy.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {



}
