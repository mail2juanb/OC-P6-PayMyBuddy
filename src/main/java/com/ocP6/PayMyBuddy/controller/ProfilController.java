package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.CreditBalanceRequest;
import com.ocP6.PayMyBuddy.dto.ProfilRequest;
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
public class ProfilController {

    private final CustomerService customerService;


    @GetMapping("/profil")
    public String profil(Model model) {

        // NOTE : Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE : Demande au service de renvoyer le username du customer concerné
        String username = customerService.getUsernameById(userId);

        // NOTE : Demande au service de renvoyer l'email du customer concerné
        String email = customerService.getEmailById(userId);

        // NOTE : Envoi vers la vue
        model.addAttribute("username", username);
        model.addAttribute("email", email);

        // NOTE : Instancie l'objet pour la requête du formulaire
        model.addAttribute("profilRequest", new ProfilRequest());
        model.addAttribute("creditBalanceRequest", new CreditBalanceRequest());

        return "profil";
    }



    @PostMapping("/profil")
    public String updateProfil(@Valid @ModelAttribute("profilRequest") ProfilRequest request, BindingResult result, Model model) {

        // NOTE : Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE : Datas pour rechargement de la page
        String username = customerService.getUsernameById(userId);
        String email = customerService.getEmailById(userId);

        if(result.hasErrors()){
            List<String> messages = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();

            model.addAttribute("errorMessages", messages);
            model.addAttribute("transactionRequest", request);
            model.addAttribute("username", username);
            model.addAttribute("email", email);

            return "profil";
        }


        // NOTE : Récupère le contenu dans la request
        String usernameRequest = request.getUsername();
        String emailRequest = request.getEmail();
        String passwordRequest = request.getPassword();

        // NOTE : Demande au service de mettre à jour le customer
        try {
            customerService.updateCustomer(userId, usernameRequest, emailRequest, passwordRequest);
            return "redirect:/transfert?profil=true";                                                                   // Redirection vers la page de transfert en cas de succès
        } catch (Exception exception) {
            log.error("{} during updateCustomer: {}", exception.getClass().getSimpleName(), exception.getMessage());
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("profilRequest", request);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "profil";                                                                                            // Retourner la vue pour affichage de ou des erreurs
        }

    }



    @PostMapping("/creditBalance")
    public String creditBalance(@Valid @ModelAttribute("creditBalanceRequest") CreditBalanceRequest request, BindingResult result, Model model) {

        Long userId = SecurityTools.getConnectedUser().getId();

        if(result.hasErrors()){
            List<String> messages = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();

            model.addAttribute("errorMessages", messages);
            return "profil";
        }

        try {
            customerService.creditBalance(userId, request.getBalance());                     // Appel du service pour créditer la balance
            return "redirect:/transfert?balanceSuccess=true";                               // Redirection avec succès
        } catch (Exception exception) {
            log.error("{} Balance credit error : {}", exception.getClass().getSimpleName(), exception.getMessage());
            model.addAttribute("errorMessage", exception.getMessage());
            return "profil";                                                                // Retourner à la page de profil avec message d'erreur
        }
    }

}