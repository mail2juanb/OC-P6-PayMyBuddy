package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.dto.AddUserRequest;
import com.ocP6.PayMyBuddy.service.CustomerServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class UserController {

    @Autowired
    private CustomerServiceImpl userService;


    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("add_new_user") AddUserRequest addUserRequest) {
        userService.createUser(addUserRequest);
        return "redirect:/";
    }


}
