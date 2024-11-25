package com.ocP6.PayMyBuddy.repository;

import com.ocP6.PayMyBuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.connections FROM User u WHERE u.id = :id")
    List<User> getFriendsById(@Param("id") Long id);

}
