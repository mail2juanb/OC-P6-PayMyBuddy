package com.ocP6.PayMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    public String loginRequest(@RequestParam String email,
                               @RequestParam String password) {
        //TODO: Remplacer par un objet avec des droits - cf projet Safe

        return "redirect:/login?success";
    }
}
