package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.service.CustomerService;
import com.ocP6.PayMyBuddy.service.TransfertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TransfertController {

    private final CustomerService customerService;

    private final TransfertService transfertService;


    @GetMapping("/transfert")
    public String transfert(Model model) {

        // NOTE: Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE: Demande au service de récupérer la liste des amis du customer connecté
        // TODO: Déplacer vers TransfertService
        List<Customer> connections = customerService.getConnectionsById(userId);

        // NOTE: Demande au service d'envoyer la liste des amis de l'utilisateur concerné
        // TODO: Déplacer vers TransfertService
        List<Transaction> transactions = customerService.getTransactionsById(userId);

        // NOTE Demande au service de renvoyer la balance du customer connecté
        // TODO: Déplacer vers TransfertService
        BigDecimal balance = customerService.getBalanceById(userId);

        // NOTE: Envoi des listes à la vue concernée
        model.addAttribute("connections", connections);
        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", balance);

        return "transfert";
    }


    @PostMapping("/transfert")
    public String processTransfert(
                    @RequestParam("relation") Long relationId,
                    @RequestParam("description") String description,
                    @RequestParam("amount") BigDecimal amount) {

        // NOTE: Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE: Demande au service d'ajouter une transaction
        // TODO: Ajouter les levées d'erreurs et rediriger vers les pages correspondantes
        try {
            transfertService.createTransfert(userId, relationId, description, amount);
            return "redirect:/transfert?transaction=true";                  // Redirection vers la page de transfert en cas de succès
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}
