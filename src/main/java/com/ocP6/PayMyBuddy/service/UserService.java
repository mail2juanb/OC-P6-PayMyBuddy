package com.ocP6.PayMyBuddy.service;

import com.ocP6.PayMyBuddy.model.User;

import java.util.List;

public interface UserService {

    String findUsernameById(Long id);

    long countUsers();

    List<User> findAll();

    List<User> getConnectionsByUserId(Long userId);

    List<User> getConnectionsByUserIdMethodB(Long userId);

    void save(User user);

}
