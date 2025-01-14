package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransfertService {


    List<Transaction> getTransactionsById(Long userId);

    void createTransfert(Long userId, Long relationId, String description, BigDecimal amount);

}
