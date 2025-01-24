package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.ConnectionRequest;
import com.ocP6.PayMyBuddy.dto.TransfertRequest;
import com.ocP6.PayMyBuddy.exception.*;
import com.ocP6.PayMyBuddy.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String addConnection(@Valid @ModelAttribute("connectionRequest") ConnectionRequest request) {

        // NOTE: Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE: Demande au service d'ajouter une connection
        try {
            customerService.addConnection(userId, request.getEmail());
            return "redirect:/transfert?connection=true";                                       // Redirection vers la page de transfert en cas de succès
        } catch (NotFoundCustomerException exception) {
            log.error("NotFoundCustomerException during addConnection: {}", exception.getMessage());
            return "redirect:/connection?errornotfound=true";                                   // Retourner la vue pour affichage de l'erreur - NotFoundCustomerException
        } catch (ConflictYourselfException exception) {
            log.error("ConflictYourselfException during addConnection: {}", exception.getMessage());
            return "redirect:/connection?errorconflictyourself=true";                           // Retourner la vue pour affichage de l'erreur - ConflictYourselfException
        } catch (ConflictConnectionException exception) {
            log.error("ConflictConnectionException during addConnection: {}", exception.getMessage());
            return "redirect:/connection?errorconflictalready=true";                           // Retourner la vue pour affichage de l'erreur - ConflictConnectionException
        }

    }


}
