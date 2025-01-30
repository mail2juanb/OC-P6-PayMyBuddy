package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.TransfertRequest;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.service.CustomerService;
import com.ocP6.PayMyBuddy.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TransfertController {

    private final CustomerService customerService;

    private final TransactionService transactionService;


    @GetMapping("/transfert")
    public String transfert(Model model) {

        // NOTE : Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE : Demande au service de récupérer la liste des amis du customer connecté
        List<Customer> connections = customerService.getConnectionsById(userId);

        // NOTE : Demande au service d'envoyer la liste des amis de l'utilisateur concerné
        List<Transaction> transactions = transactionService.getTransactionsById(userId);

        // NOTE : Demande au service de renvoyer la balance du customer connecté
        BigDecimal balance = customerService.getBalanceById(userId);

        // NOTE : Envoi des listes à la vue concernée
        model.addAttribute("connections", connections);
        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", balance);

        // NOTE : Instancie l'objet pour la requête du formulaire
        model.addAttribute("transfertRequest", new TransfertRequest());

        return "transfert";
    }


    @PostMapping("/transfert")
    public String processTransfert(@Valid @ModelAttribute("transfertRequest") TransfertRequest request, BindingResult result, Model model) {

        // NOTE : Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE : Datas pour rechargement de la page
        List<Customer> connections = customerService.getConnectionsById(userId);
        List<Transaction> transactions = transactionService.getTransactionsById(userId);
        BigDecimal balance = customerService.getBalanceById(userId);

        if(result.hasErrors()){
            List<String> messages = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();

            model.addAttribute("errorMessages", messages);
            model.addAttribute("transactionRequest", request);
            model.addAttribute("connections", connections);
            model.addAttribute("transactions", transactions);
            model.addAttribute("balance", balance);

            return "transfert";
        }

        // NOTE : Récupère les valeurs du transfertRequest
        Long relationId = request.getRelationId();
        String description = request.getDescription();
        BigDecimal amount = request.getAmount();

        // NOTE : Demande au service d'ajouter une transaction
        try {
            transactionService.createTransfert(userId, relationId, description, amount);
            return "redirect:/transfert?transaction=true";                                                              // Redirection vers la page de transfert en cas de succès
        } catch (Exception exception) {
            log.error("{} during createTransfert: {}", exception.getClass().getSimpleName(), exception.getMessage());
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("transfertRequest", request);
            model.addAttribute("connections", connections);
            model.addAttribute("transactions", transactions);
            model.addAttribute("balance", balance);
            return "transfert";                                                                                         // Retourner la vue pour affichage de ou des erreurs
        }

    }

}
