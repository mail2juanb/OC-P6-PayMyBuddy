package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.*;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;




    public void createCustomer(String username, String email, String password) {

        // NOTE : Si l'email existe déjà dans la bdd alors, on lève une Exception
        if (customerRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ConflictException("Email already exist -> " + email);
        }

        String hashedPassword = passwordEncoder.encode(password);

        final Customer customer = new Customer(username, email, hashedPassword);

        customerRepository.save(customer);
    }



    public List<Customer> getConnectionsById(Long userId) {

        return customerRepository.findById(userId)
                .map(Customer::getConnections)
                .orElseThrow(() -> new NotFoundException("Id not found -> " + userId));

    }



    public void addConnection(Long customerId, String email) {

        // NOTE: Récupère le customer correspondant à l'id.
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundCustomerException("Customer not found with id -> " + customerId));

        // NOTE: Vérifier si l'email existe.
        Customer addCustomer = customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundCustomerException("Customer not found with email -> " + email));

        // NOTE: Récupère l'id correspondant à l'email
        Long addCustomerId = addCustomer.getId();

        // NOTE: Vérifie que ce n'est pas le même customer.
        if (addCustomerId.equals(customerId)) {
            throw new ConflictYourselfException("You can't be connected to yourself");
        }

        // NOTE: Vérifier que les 2 Customer ne sont pas déjà amis
        List<Customer> connections = customer.getConnections();
        if (connections.stream().anyMatch(c -> c.getId().equals(addCustomerId))) {
            throw new ConflictConnectionException("You are already connected with -> " + email);
        }

        // NOTE: Ajouter la relation entre eux
        customer.getConnections().add(addCustomer);
        customerRepository.save(customer);
        addCustomer.getConnections().add(customer);
        customerRepository.save(addCustomer);

    }



    public BigDecimal getBalanceById(Long userId) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("ID not found -> " + userId));
        return customer.getBalance();

    }



    public String getUsernameById(Long userId) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundCustomerException("Customer not found with id -> " + userId));
        return customer.getUsername();

    }



    public String getEmailById(Long userId) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundCustomerException("Customer not found with id -> " + userId));
        return customer.getEmail();

    }



    public void updateCustomer(Long userId, String usernameRequest, String emailRequest, String passwordRequest) {

        // NOTE : Récupérer le Customer original
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundCustomerException("Customer not found with id -> " + userId));

        // NOTE : Vérifier que le username de la request n'est pas déjà utilisé par un autre customer
        customerRepository.findByUsername(usernameRequest).ifPresent(foundCustomer -> {
            if (!foundCustomer.getId().equals(userId)) {
                throw new AlreadyTakenUsernameException("Username is already taken.");
            }
        });

        // NOTE : Vérifier que l'email de la request n'est pas utilisé par un autre customer
        customerRepository.findByEmailIgnoreCase(emailRequest).ifPresent(existingCustomer -> {
            if (!existingCustomer.getId().equals(userId)) {
                throw new AlreadyTakenEmailException("Email is already taken.");
            }
        });

        // NOTE : Mettre à jour les informations
        customer.setUsername(usernameRequest);
        customer.setEmail(emailRequest);
        customer.setPassword(passwordEncoder.encode(passwordRequest));

        // NOTE : Sauvegarder les modifications
        customerRepository.save(customer);


    }

}
