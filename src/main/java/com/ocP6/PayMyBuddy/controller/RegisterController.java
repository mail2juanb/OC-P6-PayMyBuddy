package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.dto.RegisterRequest;
import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String registerCustomer(@Valid @ModelAttribute("registerRequest") RegisterRequest request) {

        // NOTE: Demande au Service d'enregistrer un nouveau customer
        try {
            customerService.createCustomer(request.getUsername(), request.getEmail(), request.getPassword());
            return "redirect:/login?success";                               // Redirection vers la page de connexion en cas de succès
        } catch (ConflictException exception) {
            log.error("Error during registration: {}", exception.getMessage());
            return "redirect:/register?error=true";                         // Retourner la vue pour affichage de l'erreur - ConflictException
        }

    }

}
