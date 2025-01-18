package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.TransfertRequest;
import com.ocP6.PayMyBuddy.exception.ConflictException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
import com.ocP6.PayMyBuddy.exception.UnhandledException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.service.CustomerService;
import com.ocP6.PayMyBuddy.service.TransfertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TransfertController {

    private final CustomerService customerService;

    private final TransfertService transfertService;


    @GetMapping("/transfert")
    public String transfert(Model model) {

        // NOTE : Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE : Demande au service de récupérer la liste des amis du customer connecté
        List<Customer> connections = customerService.getConnectionsById(userId);

        // NOTE : Demande au service d'envoyer la liste des amis de l'utilisateur concerné
        List<Transaction> transactions = transfertService.getTransactionsById(userId);

        // NOTE : Demande au service de renvoyer la balance du customer connecté
        BigDecimal balance = customerService.getBalanceById(userId);

        // NOTE : Instanciation de l'objet pour dropdown sur default
        TransfertRequest transfertRequest = new TransfertRequest();
        transfertRequest.setRelationId(-1L);

        // NOTE : Envoi des listes à la vue concernée
        model.addAttribute("transfertRequest", transfertRequest);
        model.addAttribute("connections", connections != null ? connections : Collections.emptyList());
        model.addAttribute("transactions", transactions != null ? transactions : Collections.emptyList());
        model.addAttribute("balance", balance);

        return "transfert";
    }


    @PostMapping("/transfert")
    public String processTransfert(@Valid @ModelAttribute("transfertRequest") TransfertRequest request) {

        // NOTE : Récupère les valeurs
        Long relationId = request.getRelationId();
        String description = request.getDescription();
        BigDecimal amount = request.getAmount();


        // NOTE : Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE : Demande au service d'ajouter une transaction
        try {
            transfertService.createTransfert(userId, relationId, description, amount);
            return "redirect:/transfert?transaction=true";                          // Redirection vers la page de transfert en cas de succès
        } catch (NotFoundException exception) {
            log.error("NotFoundException during createTransfert: {}", exception.getMessage());
            if (exception.getMessage().contains("customer not found")) {
                return "redirect:/transfert?errorCustomerNotFound=true";           //Le Customer n'existe pas
            } else if (exception.getMessage().contains("relation not found")) {
                return "redirect:/transfert?errorRelationNotFound=true";           //La Relation n'existe pas
            }
        } catch (ConflictException exception) {
            log.error("ConflictException during createTransfert: {}", exception.getMessage());
            if (exception.getMessage().contains("yourself")) {
                return "redirect:/transfert?errorConflictYourself=true";           //Le customer ne peut être ami avec lui meme
            } else if (exception.getMessage().contains("connection")) {
                return "redirect:/transfert?errorConflictConnection=true";         //Le customer et la relation ne sont pas amis
            } else if (exception.getMessage().contains("exceeds")) {
                return "redirect:/transfert?errorConflictExceeds=true";            //Le montant à transférer dépasse le solde
            }
        }
        // NOTE : En dernier recours, lever une exception pour qu'elle soit gérée par ApiExceptionHandler
        throw new UnhandledException("Unhandled exception during transfer process");
    }

}
