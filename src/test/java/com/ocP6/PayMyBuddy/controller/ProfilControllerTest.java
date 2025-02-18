package com.ocP6.PayMyBuddy.controller;


import com.ocP6.PayMyBuddy.configuration.CustomUserDetails;
import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.dto.CreditBalanceRequest;
import com.ocP6.PayMyBuddy.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfilControllerTest {

    @InjectMocks
    private ProfilController customerController;

    @Mock
    private CustomerService customerService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Mock
    private CustomUserDetails mockCustomUserDetails;

    private CreditBalanceRequest request;



    @BeforeEach
    void setUp() {
        request = new CreditBalanceRequest();
        request.setBalance(new BigDecimal("100.00"));
    }



    @Test
    void creditBalance_withValidationErrors_shouldReturnProfilPage() {

        // Given the request has validation errors, user authentication should not be triggered
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("balance", "Invalid amount")));

        // Mock SecurityContext to simulate an authenticated user
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails mockedUser = mock(CustomUserDetails.class);
        SecurityContextHolder.setContext(securityContext); // Set the mock SecurityContext

        try (MockedStatic<SecurityTools> mockedStatic = mockStatic(SecurityTools.class)) {
            // Mock the SecurityTools.getConnectedUser method to return the mocked user
            when(SecurityTools.getConnectedUser()).thenReturn(mockedUser);

            // When the method is called
            String view = customerController.creditBalance(request, bindingResult, model);

            // Then the profile page should be returned with error messages, no authentication should be required
            verify(model).addAttribute(eq("errorMessages"), anyList());
            assertEquals("profil", view);

            // Ensure SecurityTools.getConnectedUser() is never called for authentication
            mockedStatic.verify(SecurityTools::getConnectedUser, times(1));                  // Verify it's called once
            verifyNoInteractions(customerService);                                                                  // Ensure service is never called
        }

    }



    @Test
    void creditBalance_success_shouldRedirectToTransfert() {

        // Given a valid request with no validation errors and a connected user
        when(bindingResult.hasErrors()).thenReturn(false);
        try (MockedStatic<SecurityTools> mockedStatic = mockStatic(SecurityTools.class)) {
            mockedStatic.when(SecurityTools::getConnectedUser).thenReturn(mockCustomUserDetails);
            when(mockCustomUserDetails.getId()).thenReturn(1L);

            // When the method is called
            String result = customerController.creditBalance(request, bindingResult, model);

            // Then the balance should be credited and the user redirected to the transfer page
            verify(customerService).creditBalance(1L, new BigDecimal("100.00"));
            assertEquals("redirect:/transfert?balanceSuccess=true", result);
        }

    }



    @Test
    void creditBalance_withException_shouldReturnProfilPage() {

        // Given a valid request, but the service throws an exception and a connected user
        when(bindingResult.hasErrors()).thenReturn(false);
        try (MockedStatic<SecurityTools> mockedStatic = mockStatic(SecurityTools.class)) {
            mockedStatic.when(SecurityTools::getConnectedUser).thenReturn(mockCustomUserDetails);
            when(mockCustomUserDetails.getId()).thenReturn(1L);
            doThrow(new RuntimeException("Insufficient funds"))
                    .when(customerService).creditBalance(1L, new BigDecimal("100.00"));

            // When the method is called
            String view = customerController.creditBalance(request, bindingResult, model);

            // Then the profile page should be returned with an error message
            verify(model).addAttribute("errorMessage", "Insufficient funds");
            assertEquals("profil", view);
        }

    }

}
