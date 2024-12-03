package com.ocP6.PayMyBuddy.repository;

import com.ocP6.PayMyBuddy.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // TODO: A supprimer, ce n'est pas la bonne façon de faire
    @Query("SELECT u.connections FROM Customer u WHERE u.id = :id")
    List<Customer> getFriendsById(@Param("id") Long id);



    Optional<Customer> findByEmailIgnoreCase(String email);

}
