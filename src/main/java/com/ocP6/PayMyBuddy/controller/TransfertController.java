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


    /**
     * GET /transfert
     * This URL should return the transfer page for the logged-in customer, including the list of their connections (friends),
     * their past transactions, and their available balance.
     * The following data will be provided to the view:
     * - connections: A list of the customer's friends (connections).
     * - transactions: A list of the customer's past transactions.
     * - balance: The current balance of the customer.
     * Additionally, an empty TransfertRequest object will be instantiated for the transfer form in the view.
     *
     * @param model The model object used to pass data to the view.
     * @return String "transfert" (the name of the view to render).
     */


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



    /**
     * POST /transfert
     * This URL handles the transfer request by processing the user's input from the transfert form.
     * It performs the following actions:
     * - Validates the form input (TransfertRequest) and checks for errors.
     * - If validation fails, it reloads the page with error messages and the current user data (connections, transactions, balance).
     * - If validation passes, it creates a new transaction for the customer by calling the transaction service.
     * - In case of a successful transaction creation, the user is redirected to the transfert page with a success message.
     * - If an exception occurs during transaction creation, the error message is displayed on the page.
     *
     * @param request The transfert request containing the transfer details (validated with @Valid).
     * @param result The binding result used to check if there were validation errors.
     * @param model The model object used to pass data to the view.
     * @return String "transfert" (the name of the view to render), or a redirect to the same URL in case of success.
     */

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
