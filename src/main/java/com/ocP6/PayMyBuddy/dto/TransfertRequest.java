package com.ocP6.PayMyBuddy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransfertRequest {


    @NotNull(message = "La relation ne peut pas être nulle" )
    private Long relationId;

    @NotBlank(message = "La description ne peut pas être vide")
    private String description;

    @NotNull(message = "Le montant ne peut pas être null")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount = BigDecimal.ZERO;

}
