package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.dto.RegisterRequest;
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

@Controller
@Slf4j
@RequiredArgsConstructor
public class RegisterController {

    private final CustomerService customerService;



    @GetMapping("/register")
    public String register(Model model) {

        // NOTE : Instancie l'objet pour la requête du formulaire
        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }


    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute("registerRequest") RegisterRequest request, BindingResult result, Model model) {

        if(result.hasErrors()){
            List<String> messages = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();

            model.addAttribute("errorMessages", messages);
            model.addAttribute("registerRequest", request);

            return "register";
        }

        // NOTE : Demande au Service d'enregistrer un nouveau customer
        try {
            customerService.createCustomer(request.getUsername(), request.getEmail(), request.getPassword());
            return "redirect:/login?success";                                                                           // Redirection vers la page de connexion en cas de succès
        } catch (Exception exception) {
            log.error("{} during createCustomer: {}", exception.getClass().getSimpleName(), exception.getMessage());
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("registerRequest", request);
            return "register";                                                                                          // Retourner la vue pour affichage de ou des erreurs
        }

    }

}
