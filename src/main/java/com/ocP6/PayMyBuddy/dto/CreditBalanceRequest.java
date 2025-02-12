package com.ocP6.PayMyBuddy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditBalanceRequest {


    @NotNull(message = "Le montant ne peut pas être null")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal balance;


}
