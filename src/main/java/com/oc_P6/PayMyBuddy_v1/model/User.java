package com.oc_P6.PayMyBuddy_v1.model;

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


    @Column(
            //name = "username",    // Pas besoin de le specifier explicitement, il prend le nom de la colonne par defaut
            nullable = false,
            unique = true,
            length = 100
    )
    private String username;



    @Column(
            //name = "email",       // Pas besoin de le specifier explicitement, il prend le nom de la colonne par defaut
            nullable = false,
            unique = true,
            length = 100
    )
    private String email;



    @Column(
            //name = "password",        // Pas besoin de le specifier explicitement, il prend le nom de la colonne par defaut
            nullable = false
            //length = 255 - Pas besoin de le specifier car cest par defaut
    )
    private String password;



    @Column(
            //name = "balance",         // Pas besoin de le specifier explicitement, il prend le nom de la colonne par defaut
            precision = 65,             // Cest le maximum possible
            scale = 2
    )
    private BigDecimal balance = BigDecimal.ZERO;


    // Relation avec les transactions en tant que Sender
    @OneToMany(
            mappedBy = "sender",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Transaction> sentTransactions = new ArrayList<>();



    // Relation avec les transactions en tant que Receiver
    @OneToMany(
            mappedBy = "receiver",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Transaction> receivedTransactions = new ArrayList<>();



    // Relation many-to-many avec d'autres users via la table Connections
    @ManyToMany
    @JoinTable(
            name = "connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> connections = new ArrayList<>();


}
