package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.service.CustomerService;
import com.ocP6.PayMyBuddy.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TransfertController {

    private final CustomerService customerService;

    private final TransactionService transactionService;


    @GetMapping("/transfert")
    public String transfert(Model model) {

        // NOTE: Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE: Demande au service de récupérer la liste des amis du customer connecté
        List<Customer> connections = customerService.getConnectionsById(userId);

        // NOTE: Demande au service d'envoyer la liste des amis de l'utilisateur concerné
        List<Transaction> transactions = customerService.getTransactionsById(userId);

        // NOTE: Envoi des listes à la vue concernée
        model.addAttribute("connections", connections);
        model.addAttribute("transactions", transactions);

        return "transfert";
    }

}
