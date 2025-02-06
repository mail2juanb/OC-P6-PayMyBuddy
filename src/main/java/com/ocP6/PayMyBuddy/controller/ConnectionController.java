package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.ConnectionRequest;
import com.ocP6.PayMyBuddy.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConnectionController {

    private final CustomerService customerService;



    @GetMapping("/connection")
    public String connection(Model model) {

        // NOTE : Instancie l'objet pour la requête du formulaire
        model.addAttribute("connectionRequest", new ConnectionRequest());

        return "connection";
    }



    @PostMapping("/connection")
    public String addConnection(@Valid @ModelAttribute("connectionRequest") ConnectionRequest request, BindingResult result, Model model) {

        // NOTE : Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        if(result.hasErrors()){
            List<String> messages = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();

            model.addAttribute("errorMessages", messages);
            model.addAttribute("connectionRequest", request);

            return "connection";
        }

        // NOTE : Demande au service d'ajouter une connection
        try {
            customerService.addConnection(userId, request.getEmail());
            return "redirect:/transfert?connection=true";                                                               // Redirection vers la page de transfert en cas de succès
        } catch (Exception exception) {
            log.error("{} during addConnection: {}", exception.getClass().getSimpleName(), exception.getMessage());
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("connectionRequest", request);
            return "connection";                                                                                        // Retourner la vue pour affichage de ou des erreurs
        }

    }


}
