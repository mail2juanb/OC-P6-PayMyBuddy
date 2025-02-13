package com.ocP6.PayMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    /**
     * GET /
     * This URL redirects the user to the login page.
     * It is used as the root URL to ensure that the user is redirected to the /login endpoint for authentication.
     *
     * @return String "redirect:/login" (the redirection to the login page).
     */

    @GetMapping("/")
    public String loginRoot() {
        return "redirect:/login";
    }



    /**
     * GET /login
     * This URL returns the login page where users can authenticate themselves.
     * It renders the login view, typically displaying a login form.
     *
     * @return String "login" (the name of the login view to render).
     */

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // NOTE : Le post est entièrement géré par SpringSecurityConfig

}
