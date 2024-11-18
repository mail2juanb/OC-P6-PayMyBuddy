package com.oc_P6.PayMyBuddy_v1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Entity
@Table(name = "transaction")
@Data                               // Génère les getters, setters, toString, equals, et hashCode
@NoArgsConstructor                  // Génère un constructeur sans arguments
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne
    @JoinColumn(
            //name = "sender",            // Pas besoin de le specifier explicitement, il prend le nom de la colonne par defaut
            nullable = false
    )
    private User sender;



    @ManyToOne
    @JoinColumn(
            //name = "receiver",            // Pas besoin de le specifier explicitement, il prend le nom de la colonne par defaut
            nullable = false
    )
    private User receiver;



    @Column(
            //name = "description",           // Pas besoin de le specifier explicitement, il prend le nom de la colonne par defaut
            //nullable = true                 // Pas besoin de le spécifier cest pas defaut
            //length = 255                    // Pas besoin de le specifier car cest par defaut
    )
    private String description;


    @Column(
            //name = "amout"                  // Pas besoin de le specifier explicitement, il prend le nom de la colonne par defaut
            nullable = false,
            precision = 65,                   // Cest le maximum possible
            scale = 2
    )
    @DecimalMin(
            value = "0.01",
            //inclusive = true,               // Pas besoin de le specifier, c'est inclusive true par defaut
            message = "Amount must be greater than 0"
    )
    private BigDecimal amout;

}
