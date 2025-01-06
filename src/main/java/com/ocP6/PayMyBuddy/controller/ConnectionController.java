package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
import com.ocP6.PayMyBuddy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConnectionController {

    private final CustomerService customerService;



    @GetMapping("/connection")
    public String connection(Principal principal, Model model) {
        return "connection";
    }

    @PostMapping("/connection")
    public String addConnection(@RequestParam String email, Principal principal, Model model) {

        // NOTE: Récupère le username
        String username = principal.getName();

        // NOTE: Demande au service d'ajouter une connection
        try {
            customerService.addConnection(username, email);
            return "redirect:/transfert?connection=true";                       // Redirection vers la page de transfert en cas de succès
        } catch (NotFoundException exception) {
            log.error("NotFoundException during add a friend: {}", exception.getMessage());
            return "redirect:/connection?errornotfound=true";                           // Retourner la vue pour affichage de l'erreur - NotFoundException
        } catch (ConflictException exception) {
            log.error("ConflictException during add a friend: {}", exception.getMessage());
            return "redirect:/connection?errorconflict=true";                           // Retourner la vue pour affichage de l'erreur - ConflictException
        }
    }


}
