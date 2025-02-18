package com.ocP6.PayMyBuddy.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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


    @OneToMany(mappedBy = "sender")
    private List<Transaction> sentTransactions = new ArrayList<>();


    @OneToMany(mappedBy = "receiver")
    private List<Transaction> receivedTransactions = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "users_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connections_id")
    )
    private List<Customer> connections = new ArrayList<>();



    public Customer(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
