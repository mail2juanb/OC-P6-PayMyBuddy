package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;


    public void createCustomer(String username, String email, String password){

        log.debug("\n");
        log.debug(" == createCustomer == ");
        log.debug("username = " + username);
        log.debug("email = " + email);
        log.debug("password = " + password);


        // NOTE: Si l'email existe déjà dans la bdd alors on lève une ConflictException
        if(customerRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ConflictException("Email already exist -> " + email);

        }

        String hashedPassword = passwordEncoder.encode(password);

        final Customer customer = Customer.builder()
                .username(username)
                .email(email)
                .password(hashedPassword)
                //FIXME: Pourquoi j'obtiens null dans la BDD alors que ça devrait être 0...
                //.balance(BigDecimal.valueOf(0.00))               // Facultatif, balance à 0 par défaut
                .build();

        customerRepository.save(customer);
    }



    public List<Customer> getConnectionsByUsername(String username) {

        // NOTE: Récupérer le Customer connecté + la liste de ses amis (connections)
        log.debug("\n");
        Optional<Customer> customerConnected = customerRepository.findByUsername(username);

        if (customerConnected.isPresent()) {
            log.debug("L'id de l'utilisateur connecté est {}", customerConnected.get().getId().intValue());
            List<Customer> connections = customerConnected.get().getConnections();
            log.debug("Nombre d'amis de l'utilisateur connecté : {}", connections.stream().count());

            if (connections.stream().count() > 0) {
                int j = 0;
                for (Customer customer : connections) {
                    j++;
                    log.debug("{} : ami = {}", j, customer.getUsername());
                }
                log.debug("\n");
            }
            return connections;
        }
        return null;
    }



    public List<Transaction> getTransactionsByUsername(String username) {

        // NOTE: Récupérer le Customer connecté + la liste des transactions envoyées (sentTransactions)
        log.debug("\n");
        Optional<Customer> customerConnected = customerRepository.findByUsername(username);

        if (customerConnected.isPresent()) {
            List<Transaction> sentTransactions = customerConnected.get().getSentTransactions();
            log.debug("Nombre de transactions envoyées de l'utilisateur connecté : {}", sentTransactions.stream().count());

            if (sentTransactions.stream().count() > 0) {
                int i = 0;
                for (Transaction transaction : sentTransactions) {
                    i++;
                    log.debug("{} : Transaction envoyée à {}, pour un montant de {}", i, transaction.getReceiver().getUsername(), transaction.getAmount());
                }
                log.debug("\n");
            }
            return sentTransactions;
        }
        return null;
    }



    public String getEmailByUsername(String username) {

        Optional<Customer> customer = customerRepository.findByUsername(username);
        return customer.map(Customer::getEmail).orElse(null);
    }


}
