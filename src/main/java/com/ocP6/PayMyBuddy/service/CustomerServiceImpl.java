package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    // NOTE: Ecrire le test passant                         - OK
    // NOTE: Ecrire le test non passant : ConflictException - OK
    public void createCustomer(String username, String email, String password){

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

        return customerRepository.findByUsername(username)
                .map(Customer::getEmail)
                .orElseThrow(() -> new NotFoundException("Username not found -> " + username));
    }



    // FIXME: Ecrire le test passant                                             - NoOk - LazyInitializationException
    // NOTE: Ecrire le test non passant : NotFoundException - Id not found      - OK
    // NOTE: Ecrire le test non passant : NotFoundException - Email not found   - OK
    // NOTE: Ecrire le test non passant : ConflictException - yourself          - OK
    // NOTE: Ecrire le test non passant : ConflictException - already           - OK
    @Transactional
    public void addConnection(Long customerId, String email) {

        // NOTE: Récupère le customer correspondant à l'id.
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Id not found -> " + customerId));

        // NOTE: Vérifier si l'email existe.
        Customer addCustomer = customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Email not found -> " + email));

        // NOTE: Récupère l'id correspondant à l'email
        Long addCustomerId = addCustomer.getId();

        // NOTE: Vérifie que ce n'est pas le même customer.
        if (addCustomerId.equals(customerId)) {
            throw new ConflictException("You can't be connected to yourself");
        }

        // NOTE: Vérifier que les 2 Customer ne sont pas déjà amis
        List<Customer> connections = customer.getConnections();

        if(connections.stream().anyMatch(c -> c.getId().equals(addCustomerId))) {
            throw new ConflictException("You are already connected with -> " + email);
        }

        // NOTE: Ajouter la relation entre eux
        customer.getConnections().add(addCustomer);
        customerRepository.save(customer);
        addCustomer.getConnections().add(customer);
        customerRepository.save(addCustomer);

    }

}
