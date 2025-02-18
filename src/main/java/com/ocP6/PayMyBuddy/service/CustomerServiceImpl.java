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

        if (customerRepository.findByUsername(username).isPresent()) {
            throw new AlreadyTakenUsernameException("Le nom d'utilisateur est déjà utilisé, veuillez en choisir un autre");
        }

        if (customerRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new AlreadyTakenEmailException("L'email est déjà utilisé, veuillez en choisir un autre");
        }

        String hashedPassword = passwordEncoder.encode(password);

        final Customer customer = new Customer(username, email, hashedPassword);

        customerRepository.save(customer);
    }



    public List<Customer> getConnectionsById(Long userId) {

        return customerRepository.findById(userId)
                .map(Customer::getConnections)
                .orElseThrow(() -> new NotFoundException("L'ID est introuvable : " + userId));

    }



    public void addConnection(Long customerId, String email) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + customerId));

        Customer addCustomer = customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet email : " + email));

        Long addCustomerId = addCustomer.getId();

        if (addCustomerId.equals(customerId)) {
            throw new ConflictYourselfException("Vous ne pouvez être en relation avec vous même !");
        }

        List<Customer> connections = customer.getConnections();
        if (connections.stream().anyMatch(c -> c.getId().equals(addCustomerId))) {
            throw new ConflictConnectionException("Vous êtes déjà en relation avec cet utilisateur : " + email);
        }

        customer.getConnections().add(addCustomer);
        customerRepository.save(customer);
        addCustomer.getConnections().add(customer);
        customerRepository.save(addCustomer);

    }



    public BigDecimal getBalanceById(Long userId) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + userId));
        return customer.getBalance();

    }



    public String getUsernameById(Long userId) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + userId));
        return customer.getUsername();

    }



    public String getEmailById(Long userId) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + userId));
        return customer.getEmail();

    }



    public void updateCustomer(Long userId, String usernameRequest, String emailRequest, String passwordRequest) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + userId));

        customerRepository.findByUsername(usernameRequest).ifPresent(foundCustomer -> {
            if (!foundCustomer.getId().equals(userId)) {
                throw new AlreadyTakenUsernameException("Ce nom d'utilisateur est déjà utilisé. Veuillez en choisir un autre.");
            }
        });

        customerRepository.findByEmailIgnoreCase(emailRequest).ifPresent(existingCustomer -> {
            if (!existingCustomer.getId().equals(userId)) {
                throw new AlreadyTakenEmailException("Cet email est déjà utilisé. Veuillez en choisir un autre.");
            }
        });

        customer.setUsername(usernameRequest);
        customer.setEmail(emailRequest);
        customer.setPassword(passwordEncoder.encode(passwordRequest));

        customerRepository.save(customer);

    }



    public void creditBalance(Long userId, BigDecimal amount) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + userId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif.");
        }

        customer.setBalance(customer.getBalance().add(amount));
        customerRepository.save(customer);

    }

}
