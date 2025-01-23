package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.TransfertRequest;
import com.ocP6.PayMyBuddy.exception.*;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.service.CustomerService;
import com.ocP6.PayMyBuddy.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        model.addAttribute("transfertRequest", new TransfertRequest());
        model.addAttribute("connections", connections);
        model.addAttribute("transactions", transactions);
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
            transactionService.createTransfert(userId, relationId, description, amount);
            return "redirect:/transfert?transaction=true";                          // Redirection vers la page de transfert en cas de succès
        } catch (NotFoundCustomerException exception) {
            log.error("NotFoundCustomerException during createTransfert: {}", exception.getMessage());
            return "redirect:/transfert?errorCustomerNotFound=true";                //Le Customer n'existe pas
        } catch (NotFoundRelationException exception) {
            log.error("NotFoundRelationException during createTransfert: {}", exception.getMessage());
            return "redirect:/transfert?errorRelationNotFound=true";                //La Relation n'existe pas
        } catch (ConflictYourselfException exception) {
            log.error("ConflictYourselfException during createTransfert: {}", exception.getMessage());
            return "redirect:/transfert?errorConflictYourself=true";                //Le customer ne peut être ami avec lui meme
        } catch (ConflictConnectionException exception) {
            log.error("ConflictConnectionException during createTransfert: {}", exception.getMessage());
            return "redirect:/transfert?errorConflictConnection=true";              //Le customer et la relation ne sont pas amis
        } catch (ConflictExceedsException exception) {
            log.error("ConflictExceedsException during createTransfert: {}", exception.getMessage());
            return "redirect:/transfert?errorConflictExceeds=true";            //Le montant à transférer dépasse le solde
        }

        // FIXME: Si c'est plus utile, on peut le supprimer
        // NOTE : En dernier recours, lever une exception pour qu'elle soit gérée par ApiExceptionHandler
        //throw new UnhandledException("Unhandled exception during transfer process");
    }

}
