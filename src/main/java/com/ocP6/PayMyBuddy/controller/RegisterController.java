package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RegisterController {

    private final CustomerService customerService;



    @GetMapping("/register")
    public String register() {
        return "register";
    }


    @PostMapping("/register")
    public String registerCustomer(@RequestParam String username,
                                   @RequestParam String email,
                                   @RequestParam String password) {


        // NOTE: Demande au Service d'enregistrer un nouveau compte
        try {
            customerService.createCustomer(username, email, password);
            return "redirect:/login?success";                               // Redirection vers la page de connexion en cas de succès
        } catch (ConflictException exception) {
            log.error("Error during registration: {}", exception.getMessage());
            return "redirect:/register?error=true";                         // Retourner la vue pour affichage de l'erreur
        }

    }

}
