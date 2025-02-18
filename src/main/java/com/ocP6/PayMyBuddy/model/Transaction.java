package com.ocP6.PayMyBuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.NoArgsConstructor;
import lombok.*;


import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    private Customer sender;


    @ManyToOne
    @JoinColumn(name = "receiver", nullable = false)
    private Customer receiver;


    @Column()
    private String description;


    @Column(nullable = false)
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;


    public Transaction(Customer sender, Customer receiver, String description, BigDecimal amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.description = description;
        this.amount = amount;
    }

}
