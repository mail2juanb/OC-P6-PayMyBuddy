package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.dto.AddUserRequest;
import com.ocP6.PayMyBuddy.model.User;
import com.ocP6.PayMyBuddy.repository.UserRepository;
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

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    // Méthode pour récupérer le username d'un id
    public String findUsernameById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);          // Méthode fournie par JpaRepository
        String username = optionalUser.get().getUsername();
        return username;
    }

    // Méthode pour compter les utilisateurs
    public long countUsers() {
        return userRepository.count();                                      // Méthode fournie par JpaRepository
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();                                    // Méthode fournie par JpaRepository
    }

    @Transactional
    public List<User> getConnectionsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Hibernate.initialize(user.getConnections()); // Forcer l'initialisation
        return user.getConnections();
    }

    public List<User> getConnectionsByUserIdMethodB(Long userId) {
        return userRepository.getFriendsById(userId);
    }

    public void createUser(AddUserRequest request){

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        final User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(hashedPassword)
                //.balance(BigDecimal.ZERO)               // Facultatif, balance à 0 par défaut
                .build();

        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

}
