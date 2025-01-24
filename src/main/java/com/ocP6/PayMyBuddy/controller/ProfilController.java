package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.ProfilRequest;
import com.ocP6.PayMyBuddy.exception.AlreadyTakenEmailException;
import com.ocP6.PayMyBuddy.exception.AlreadyTakenUsernameException;
import com.ocP6.PayMyBuddy.exception.NotFoundCustomerException;
import com.ocP6.PayMyBuddy.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

        return "profil";
    }



    @PostMapping("/profil")
    public String updateProfil(@Valid @ModelAttribute("profilRequest") ProfilRequest request) {

        // NOTE : Récupère l'id de l'utilisateur connecté
        Long userId = SecurityTools.getConnectedUser().getId();

        // NOTE : Récupère le username contenu dans la request
        String username = request.getUsername();

        // NOTE : Récupère l'email contenu dans la request
        String email = request.getEmail();

        // NOTE : Récupère le password contenu dans la request
        String password = request.getPassword();

        // NOTE : Demande au service de mettre à jour le customer
        try {
            customerService.updateCustomer(userId, username, email, password);
            return "redirect:/transfert?profil=true";                                       // Redirection vers la page de transfert en cas de succès
        } catch (NotFoundCustomerException exception) {
            log.error("NotFoundCustomerException during updateProfil: {}", exception.getMessage());
            return "redirect:/profil?errorCustomerNotFound=true";                           // Le Customer n'existe pas
        } catch (AlreadyTakenUsernameException exception) {
            log.error("AlreadyTakenUsernameException during updateProfil: {}", exception.getMessage());
            return "redirect:/profil?errorUsernameTaken=true";                              // Le username est déjà utilisé par un autre customer
        } catch (AlreadyTakenEmailException exception) {
            log.error("AlreadyTakenEmailException during updateProfil: {}", exception.getMessage());
            return "redirect:/profil?errorEmailTaken=true";                                 // L'email est déjà utilisé par un autre customer
        }

    }


}