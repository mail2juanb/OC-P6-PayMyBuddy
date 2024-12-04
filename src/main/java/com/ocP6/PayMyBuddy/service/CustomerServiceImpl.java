package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



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


}
