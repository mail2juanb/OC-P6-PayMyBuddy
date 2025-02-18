package com.ocP6.PayMyBuddy.controller;

import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.ConnectionRequest;
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
public class ConnectionController {

    private final CustomerService customerService;


    /**
     * GET /connection
     * This URL renders the connection page where the user can request to add a new connection.
     * It prepares a blank ConnectionRequest object for the form submission.
     *
     * @param model The model object used to pass data to the view.
     * @return String "connection" (the name of the connection view to render).
     */

    @GetMapping("/connection")
    public String connection(Model model) {

        model.addAttribute("connectionRequest", new ConnectionRequest());

        return "connection";
    }



    /**
     * POST /connection
     * This URL processes the connection form submission by validating the user's input.
     * It performs the following actions:
     * - If validation fails, it reloads the page with error messages and the entered connection details.
     * - If validation passes, it attempts to add a new connection for the logged-in customer.
     * - If successful, the user is redirected to the transfert page.
     * - If an exception occurs during the connection process, the error message is displayed on the page.
     *
     * @param request The connection request containing the connection details (validated with @Valid).
     * @param result The binding result used to check if there were validation errors.
     * @param model The model object used to pass data to the view.
     * @return String "connection" (the name of the view to render) or a redirect to the transfert page in case of success.
     */

    @PostMapping("/connection")
    public String addConnection(@Valid @ModelAttribute("connectionRequest") ConnectionRequest request, BindingResult result, Model model) {

        Long userId = SecurityTools.getConnectedUser().getId();

        if(result.hasErrors()){
            List<String> messages = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();

            model.addAttribute("errorMessages", messages);
            model.addAttribute("connectionRequest", request);

            return "connection";
        }

        try {
            customerService.addConnection(userId, request.getEmail());
            return "redirect:/transfert?connection=true";
        } catch (Exception exception) {
            log.error("{} during addConnection: {}", exception.getClass().getSimpleName(), exception.getMessage());
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("connectionRequest", request);
            return "connection";
        }

    }


}
