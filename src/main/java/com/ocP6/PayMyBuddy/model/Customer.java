package com.ocP6.PayMyBuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data                               // Génère les getters, setters, toString, equals, et hashCode
@NoArgsConstructor                  // Génère un constructeur sans arguments
@AllArgsConstructor                 // Génère un constructeur avec tous les arguments
@Builder                            // Permet d'ajouter des constructeurs personalisés
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 100)
    private String username;


    @Column(nullable = false, unique = true, length = 100)
    private String email;


    @Column(nullable = false)
    private String password;


    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;


    // Relation avec les transactions en tant que Sender
    @OneToMany(mappedBy = "sender")
    private List<Transaction> sentTransactions = new ArrayList<>();


    // Relation avec les transactions en tant que Receiver
    @OneToMany(mappedBy = "receiver")
    private List<Transaction> receivedTransactions = new ArrayList<>();


    // Relation many-to-many avec d'autres users via la table Connections. Spring s'en occupe tout seul
    @ManyToMany
    @JoinTable(
            name = "users_connections",                                                         // Nom réel de la table intermédiaire
            joinColumns = @JoinColumn(name = "user_id"),                                        // Nom réel de la colonne
            inverseJoinColumns = @JoinColumn(name = "connections_id")                           // Nom réel de l'autre colonne
    )
    private List<Customer> connections = new ArrayList<>();


    // Constructeur pour l'ajout d'un nouveau Customer dans la BDD
    public Customer(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
