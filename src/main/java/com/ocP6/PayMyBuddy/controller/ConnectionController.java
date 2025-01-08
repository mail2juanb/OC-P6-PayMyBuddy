package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
import com.ocP6.PayMyBuddy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConnectionController {

    private final CustomerService customerService;



    @GetMapping("/connection")
    public String connection() {
        return "connection";
    }



    @PostMapping("/connection")
    public String addConnection(@RequestParam String email) {

        // NOTE: Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE: Demande au service d'ajouter une connection
        try {
            customerService.addConnection(userId, email);
            return "redirect:/transfert?connection=true";                                   // Redirection vers la page de transfert en cas de succès
        } catch (NotFoundException exception) {
            log.error("NotFoundException during addConnection: {}", exception.getMessage());
            return "redirect:/connection?errornotfound=true";                               // Retourner la vue pour affichage de l'erreur - NotFoundException
        } catch (ConflictException exception) {
            log.error("ConflictException during addConnection: {}", exception.getMessage());
            if (exception.getMessage().contains("yourself")) {
                return "redirect:/connection?errorconflictyourself=true";                           // Retourner la vue pour affichage de l'erreur - ConflictException - yourself
            } else {
                return "redirect:/connection?errorconflictalready=true";                           // Retourner la vue pour affichage de l'erreur - ConflictException - Already exist
            }

        }
    }


}
