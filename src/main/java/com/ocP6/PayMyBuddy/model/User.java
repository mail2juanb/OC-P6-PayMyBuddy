package com.ocP6.PayMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data                               // Génère les getters, setters, toString, equals, et hashCode
@NoArgsConstructor                  // Génère un constructeur sans arguments
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 100)
    private String username;


    @Column(nullable = false, unique = true, length = 100)
    private String email;


    @Column(nullable = false)
    private String password;


    @Column(precision = 65, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;


    // Relation avec les transactions en tant que Sender
    @OneToMany(mappedBy = "sender")
    private List<Transaction> sentTransactions = new ArrayList<>();


    // Relation avec les transactions en tant que Receiver
    @OneToMany(mappedBy = "receiver")
    private List<Transaction> receivedTransactions = new ArrayList<>();


    // Relation many-to-many avec d'autres users via la table Connections. Spring s'en occupe tout seul
    @ManyToMany
    private List<User> connections = new ArrayList<>();


}
