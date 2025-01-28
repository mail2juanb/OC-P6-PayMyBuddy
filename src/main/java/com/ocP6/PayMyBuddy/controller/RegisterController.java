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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RegisterController {

    private final CustomerService customerService;



    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }


    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute("registerRequest") RegisterRequest request, BindingResult result, RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            List<String> messages = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();

            redirectAttributes.addFlashAttribute("errorMessages", messages);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }

        // NOTE: Demande au Service d'enregistrer un nouveau customer
        try {
            customerService.createCustomer(request.getUsername(), request.getEmail(), request.getPassword());
            return "redirect:/login?success";                               // Redirection vers la page de connexion en cas de succès
        } catch (Exception exception) {
            log.error("{} during createTransfert: {}", exception.getClass().getSimpleName(), exception.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";                         // Retourner la vue pour affichage de l'erreur - ConflictException
        }

    }

}
