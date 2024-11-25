package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Transaction;

import java.util.List;

public interface TransactionService {

    long countTransactions();

    List<Transaction> findAll();
}
