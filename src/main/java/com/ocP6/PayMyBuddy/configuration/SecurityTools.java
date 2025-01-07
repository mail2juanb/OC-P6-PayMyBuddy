package com.ocP6.PayMyBuddy.configuration;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityTools {

    public static CustomUserDetails getConnectedUser() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        throw new BadCredentialsException("User not connected");
    }
}
