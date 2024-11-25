package com.ocP6.PayMyBuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Data                               // Génère les getters, setters, toString, equals, et hashCode
@NoArgsConstructor                  // Génère un constructeur sans arguments
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    private User sender;


    @ManyToOne
    @JoinColumn(name = "receiver", nullable = false)
    private User receiver;


    @Column()
    private String description;


    @Column(nullable = false, precision = 65, scale = 2)
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

}
