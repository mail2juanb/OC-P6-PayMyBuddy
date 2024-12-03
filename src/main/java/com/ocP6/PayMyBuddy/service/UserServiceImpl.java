package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.dto.AddUserRequest;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;


    // Méthode pour récupérer le username d'un id
    public String findUsernameById(Long id) {
        Optional<Customer> optionalUser = customerRepository.findById(id);          // Méthode fournie par JpaRepository
        String username = optionalUser.get().getUsername();
        return username;
    }

    // Méthode pour compter les utilisateurs
    public long countUsers() {
        return customerRepository.count();                                      // Méthode fournie par JpaRepository
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();                                    // Méthode fournie par JpaRepository
    }

    @Transactional
    public List<Customer> getConnectionsByUserId(Long userId) {
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Hibernate.initialize(customer.getConnections()); // Forcer l'initialisation
        return customer.getConnections();
    }

    public List<Customer> getConnectionsByUserIdMethodB(Long userId) {
        return customerRepository.getFriendsById(userId);
    }

    public void createUser(AddUserRequest request){

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        final Customer customer = Customer.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(hashedPassword)
                //.balance(BigDecimal.ZERO)               // Facultatif, balance à 0 par défaut
                .build();

        customerRepository.save(customer);
    }

    public void save(Customer customer) {
        customerRepository.save(customer);
    }

}
