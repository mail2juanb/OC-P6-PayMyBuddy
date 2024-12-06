package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfilController {

    private final CustomerService customerService;


    @GetMapping("/profil")
    public String profil(Principal principal, Model model) {

        model.addAttribute("username", principal.getName());

        String email = customerService.getEmailByUsername(principal.getName());
        model.addAttribute("email", email);

        // Le mot de passe n'est pas affiché pour des raisons de sécurité.
        // Il est cependant demandé de le ré écrire ou de le modifier.

        return "profil";
    }

    // TODO: Implémenter le POST pour PUT le customer.


}
