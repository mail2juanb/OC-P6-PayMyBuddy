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


    /**
     * GET /profil
     * This URL retrieves and displays the profile page for the logged-in user.
     * It provides the following data to the view:
     * - username: The current username of the logged-in customer.
     * - email: The current email of the logged-in customer.
     * - profilRequest: A blank ProfilRequest object for updating the profile.
     * - creditBalanceRequest: A blank CreditBalanceRequest object for managing the credit balance.
     *
     * @param model The model object used to pass data to the view.
     * @return String "profil" (the name of the profile view to render).
     */

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



    /**
     * POST /profil
     * This URL processes the profile update form submission by validating the input data.
     * It performs the following actions:
     * - If validation fails, it reloads the profile page with error messages and the entered profile details.
     * - If validation passes, it updates the customer's profile (username, email, and password).
     * - If successful, the user is redirected to the transfert page.
     * - If an exception occurs during the profile update, the error message is displayed on the page.
     *
     * @param request The profil request containing the updated profile details (validated with @Valid).
     * @param result The binding result used to check if there were validation errors.
     * @param model The model object used to pass data to the view.
     * @return String "profil" (the name of the view to render) or a redirect to the transfert page in case of success.
     */

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



    /**
     * POST /creditBalance
     * This URL processes the credit balance form submission by validating the input data.
     * It performs the following actions:
     * - If validation fails, it reloads the profile page with error messages.
     * - If validation passes, it credits the balance of the logged-in customer with the amount specified.
     * - If successful, the user is redirected to the transfert page with a success message.
     * - If an error occurs during the credit process, the error message is displayed on the profile page.
     *
     * @param request The credit balance request containing the balance amount to credit (validated with @Valid).
     * @param result The binding result used to check if there were validation errors.
     * @param model The model object used to pass data to the view.
     * @return String "profil" (the name of the view to render) or a redirect to the transfert page in case of success.
     */

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