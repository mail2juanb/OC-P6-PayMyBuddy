package com.ocP6.PayMyBuddy.service;

import java.math.BigDecimal;

public interface TransfertService {

    void createTransfert(Long userId, Long relationId, String description, BigDecimal amount);

}
