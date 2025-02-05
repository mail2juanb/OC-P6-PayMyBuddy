package com.ocP6.PayMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


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

    // NOTE : Le post est entièrement géré par SpringSecurityConfig

}
