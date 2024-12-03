package com.ocP6.PayMyBuddy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class RegisterController {


    @GetMapping("/registerPage")
    public String registrationPage() {
        return "registerPage";
    }


    @PostMapping("/registerPage")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {


        // Logique pour sauvegarder l'utilisateur dans la base de données
        // Utilisez un service pour gérer l'encodage des mots de passe et la persistance
        // Exemple :
        // userService.registerNewUser(username, email, password);

        return "redirect:/loginPage"; // Redirection vers la page de connexion
    }

    @GetMapping("/transfertPage")
    public String loginPage() {

        log.error("transfertPage");
        return null;

    }
}
