package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.controller.requests.AddUserRequest;
import com.ocP6.PayMyBuddy.model.User;
import com.ocP6.PayMyBuddy.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;


@Controller
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    @PostMapping("/save")
    public String saveUser(@ModelAttribute("add_new_user") AddUserRequest addUserRequest) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(addUserRequest.getPassword());

        // Construction de l'objet User
        final User user = User.builder()
                        .username(addUserRequest.getUsername())
                        .email(addUserRequest.getEmail())
                        .password(hashedPassword)
                        .balance(BigDecimal.ZERO)               // Facultatif, balance à 0 par défaut
                        .build();

        userService.save(user);
        return "redirect:/";
    }


}
