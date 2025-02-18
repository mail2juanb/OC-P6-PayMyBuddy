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


    /**
     * GET /register
     * This URL serves the registration form to the user.
     * It initializes a new RegisterRequest object and sends it to the view to render the registration form.
     *
     * @param model The model object used to pass data to the view.
     * @return String "register" (the name of the view to render).
     */

    @GetMapping("/register")
    public String register(Model model) {

        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }



    /**
     * POST /register
     * This URL processes the registration form submission by validating the input data.
     * It performs the following actions:
     * - If validation fails, it reloads the registration page with error messages.
     * - If validation passes, it attempts to create a new customer in the system with the provided details.
     * - If successful, the user is redirected to the login page with a success message.
     * - If an error occurs during customer creation, the error message is displayed on the registration page.
     *
     * @param request The registration request containing the user details (validated with @Valid).
     * @param result The binding result used to check if there were validation errors.
     * @param model The model object used to pass data to the view.
     * @return String "register" (the name of the view to render) or a redirect to the login page in case of success.
     */

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

        try {
            customerService.createCustomer(request.getUsername(), request.getEmail(), request.getPassword());
            return "redirect:/login?success";
        } catch (Exception exception) {
            log.error("{} during createCustomer: {}", exception.getClass().getSimpleName(), exception.getMessage());
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("registerRequest", request);
            return "register";
        }

    }

}
