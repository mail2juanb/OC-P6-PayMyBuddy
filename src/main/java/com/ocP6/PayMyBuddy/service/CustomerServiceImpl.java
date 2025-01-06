package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
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

    // NOTE: Ecrire le test passant                         - OK
    // NOTE: Ecrire le test non passant : ConflictException - OK
    public void createCustomer(String username, String email, String password){

//        log.debug("\n");
//        log.debug(" == createCustomer == ");
//        log.debug("username = " + username);
//        log.debug("email = " + email);
//        log.debug("password = " + password);


        // NOTE: Si l'email existe déjà dans la bdd alors on lève une ConflictException
        if(customerRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ConflictException("Email already exist -> " + email);
        }

        String hashedPassword = passwordEncoder.encode(password);

        final Customer customer =  new Customer(username, email, hashedPassword);

        customerRepository.save(customer);
    }



    // NOTE: Ecrire le test passant                         - OK
    // NOTE: Ecrire le test non passant - NotFoundException - OK
    public List<Customer> getConnectionsByUsername(String username) {

//        log.debug("\n");
//        Optional<Customer> customerConnected = customerRepository.findByUsername(username);
//        if (customerConnected.isPresent()) {
//            log.debug("L'id de l'utilisateur connecté est {}", customerConnected.get().getId().intValue());
//            List<Customer> connections = customerConnected.get().getConnections();
//            log.debug("Nombre d'amis de l'utilisateur connecté : {}", connections.stream().count());
//            if (connections.stream().count() > 0) {
//                int j = 0;
//                for (Customer customer : connections) {
//                    j++;
//                    log.debug("{} : ami = {}", j, customer.getUsername());
//                }
//                log.debug("\n");
//            }
//        }

        return customerRepository.findByUsername(username)
                .map(Customer::getConnections)
                .orElseThrow(() -> new NotFoundException("Username not found -> " + username));

    }


    // NOTE: Ecrire le test passant                         - OK
    // NOTE: Ecrire le test non passant - NotFoundException - OK
    public List<Transaction> getTransactionsByUsername(String username) {

//        log.debug("\n");
//        Optional<Customer> customerConnected = customerRepository.findByUsername(username);
//        if (customerConnected.isPresent()) {
//            List<Transaction> sentTransactions = customerConnected.get().getSentTransactions();
//            log.debug("Nombre de transactions envoyées de l'utilisateur connecté : {}", sentTransactions.stream().count());
//            if (sentTransactions.stream().count() > 0) {
//                int i = 0;
//                for (Transaction transaction : sentTransactions) {
//                    i++;
//                    log.debug("{} : Transaction envoyée à {}, pour un montant de {}", i, transaction.getReceiver().getUsername(), transaction.getAmount());
//                }
//                log.debug("\n");
//            }
//        }

        return customerRepository.findByUsername(username)
                .map(Customer::getSentTransactions)
                .orElseThrow(() -> new NotFoundException("Username not found -> " + username));

    }


    // NOTE: Ecrire le test passant                         - OK
    // NOTE: Ecrire le test non passant : NotFoundException - OK
    public String getEmailByUsername(String username) {

//        Optional<Customer> customer = customerRepository.findByUsername(username);
//        return customer.map(Customer::getEmail).orElse(null);

        return customerRepository.findByUsername(username)
                .map(Customer::getEmail)
                .orElseThrow(() -> new NotFoundException("Username not found -> " + username));
    }



    // NOTE: Ecrire le test passant                         - OK
    // NOTE: Ecrire le test non passant : NotFoundException - OK
    // NOTE: Ecrire le test non passant : ConflictException - OK
    public void addConnection(String username, String email) {

//        log.debug("\n");
//        log.debug("DECLENCHEMENT PROCEDURE POUR AJOUTER UN AMI");
//        log.debug("\n");


        // NOTE: Récupère l'id du username. Pas besoin de vérifier si le username existe puisqu'il est connecté
        Optional<Customer> customer = customerRepository.findByUsername(username);
        Long customerId = customer.get().getId();

//        log.debug("\n");
//        log.debug("ID de l'Utilisateur connecté : {}", customerId.toString());
//        log.debug("\n");


        // NOTE: Vérifier si l'email existe et donc récupérer le Customer via l'email
        Optional<Customer> addCustomer = customerRepository.findByEmailIgnoreCase(email);
        if(addCustomer.isEmpty()) {
            throw new NotFoundException("Email not found -> " + email);
        } else if (addCustomer.get().getId().equals(customerId)) {
            throw new ConflictException("You can't be connected to yourself");
        }

        // NOTE: Récupère l'id correspondant à l'email
        Long addCustomerId = addCustomer.get().getId();

//        log.debug("\n");
//        log.debug("ID du customer que l'on souhaite ajouter en ami : {}", addCustomerId.toString());
//        log.debug("\n");


        // NOTE: Vérifier que les 2 Customer ne sont pas déjà amis
        List<Customer> connections = customer.get().getConnections();

//        log.debug("\n");
//        log.debug("Vérification de la liste des amis");
//        log.debug("Nombre de Customer present dans la liste : {}", connections.size());
//        for (Customer connection : connections) {
//            log.debug("Customer id : {}, Csutomer username : {}", connection.getId(), connection.getUsername());
//        }
//        log.debug("\n");

        if(connections.stream().anyMatch(c -> c.getId().equals(addCustomerId))) {
            throw new ConflictException("Already friend with -> " + email);
        }


        // NOTE: Ajouter la relation entre eux
        customer.get().getConnections().add(addCustomer.get());
        customerRepository.save(customer.get());

    }


}
