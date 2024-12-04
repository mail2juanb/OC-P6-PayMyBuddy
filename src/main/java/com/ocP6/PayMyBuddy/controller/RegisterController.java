package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
                                   @RequestParam String password,
                                   Model model) {

        // NOTE: Console pour vérifier ce qui est récupéré du formulaire
        log.debug("\n");
        log.debug(" == registerCustomer == ");
        log.debug("username = " + username);
        log.debug("email = " + email);
        log.debug("password = " + password);

        // NOTE: Envoi vers le service concerné, si une ConflictException est levée on reload la page avec ?error=true
        try {
            customerService.createCustomer(username, email, password);
            return "redirect:/login";                                       // Redirection vers la page de connexion en cas de succès

        } catch (ConflictException exception) {
            log.error("Error during registration: {}", exception.getMessage());
            return "redirect:/register?error=true";                         // Retourner la vue pour affichage de l'erreur

        }



    }

}
