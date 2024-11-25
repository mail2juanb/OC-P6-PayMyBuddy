package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public long countTransactions() {
        return transactionRepository.count();
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

}
