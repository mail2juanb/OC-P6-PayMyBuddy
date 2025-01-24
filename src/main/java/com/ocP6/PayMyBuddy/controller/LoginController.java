package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class LoginController {

    @GetMapping("/")
    public String loginRoot() {
        return "redirect:/login";
    }



    @GetMapping("/login")
    public String login() {
        return "login";
    }



    @PostMapping("/login")
    public String loginRequest(@Valid @ModelAttribute("loginRequest") LoginRequest request) {
        return "redirect:/login?success";
    }

}
