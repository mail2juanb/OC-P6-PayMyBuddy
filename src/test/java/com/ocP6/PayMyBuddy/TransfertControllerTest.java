package com.ocP6.PayMyBuddy;


import com.ocP6.PayMyBuddy.configuration.CustomUserDetails;
import com.ocP6.PayMyBuddy.configuration.SecurityTools;
import com.ocP6.PayMyBuddy.controller.TransfertController;
import com.ocP6.PayMyBuddy.dto.TransfertRequest;
import com.ocP6.PayMyBuddy.exception.NotFoundCustomerException;
import com.ocP6.PayMyBuddy.exception.NotFoundException;
import com.ocP6.PayMyBuddy.model.Customer;
import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.service.CustomerServiceImpl;
import com.ocP6.PayMyBuddy.service.TransactionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TransfertControllerTest {

    @Mock
    private CustomerServiceImpl customerService;

    @Mock
    private TransactionServiceImpl transactionService;

    @Mock
    private SecurityTools securityTools;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @InjectMocks
    TransfertController transfertController;




    @Test
    void transfert_shouldReturnTransfertViewAndAddAttributesToModel() {

        // Given
        final Long userId = 1L;
        final Customer customer = new Customer();
        customer.setId(userId);

        final List<Customer> connections = List.of(new Customer(), new Customer());
        final List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        final BigDecimal balance = new BigDecimal("100.50");

        // Simulation de l'utilisateur connecté via SecurityTools
        try (MockedStatic<SecurityTools> mockedSecurityTools = Mockito.mockStatic(SecurityTools.class)) {
            // Création d'un mock de CustomUserDetails
            CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
            when(mockUserDetails.getId()).thenReturn(userId);

            // Simulation du retour de SecurityTools.getConnectedUser()
            mockedSecurityTools.when(SecurityTools::getConnectedUser).thenReturn(mockUserDetails);

            when(customerService.getConnectionsById(userId)).thenReturn(connections);
            when(transactionService.getTransactionsById(userId)).thenReturn(transactions);
            when(customerService.getBalanceById(userId)).thenReturn(balance);

            // When
            String result = transfertController.transfert(model);

            // Then
            assertEquals("transfert", result);
            verify(model).addAttribute("connections", connections);
            verify(model).addAttribute("transactions", transactions);
            verify(model).addAttribute("balance", balance);
            verify(model).addAttribute(eq("transfertRequest"), any(TransfertRequest.class));
        }

    }



    @Test
    void transfert_shouldThrowNotFoundException_whenUserIdNotFound() {

        // Given Un ID qui n'existe pas
        final Long userId = 999L;

        try (MockedStatic<SecurityTools> mockedSecurityTools = Mockito.mockStatic(SecurityTools.class)) {
            CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
            when(mockUserDetails.getId()).thenReturn(userId);
            mockedSecurityTools.when(SecurityTools::getConnectedUser).thenReturn(mockUserDetails);

            // Simuler le cas où l'utilisateur n'existe pas
            when(customerService.getConnectionsById(userId))
                    .thenThrow(new NotFoundException("L'ID est introuvable : " + userId));

            // When & Then
            assertThrows(NotFoundException.class, () -> transfertController.transfert(model));
        }

    }



    @Test
    void transfert_shouldThrowNotFoundCustomerException_whenCustomerNotFound() {

        // Given Un ID qui n'existe pas
        final Long userId = 999L;

        try (MockedStatic<SecurityTools> mockedSecurityTools = Mockito.mockStatic(SecurityTools.class)) {
            CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
            when(mockUserDetails.getId()).thenReturn(userId);
            mockedSecurityTools.when(SecurityTools::getConnectedUser).thenReturn(mockUserDetails);

            // Simuler le cas où l'utilisateur n'existe pas
            when(transactionService.getTransactionsById(userId))
                    .thenThrow(new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + userId));

            // When & Then
            assertThrows(NotFoundCustomerException.class, () -> transfertController.transfert(model));
        }

    }



    @Test
    void transfert_shouldThrowNotFoundCustomerException_whenUserIdNotFound() {

        // Given Un ID qui n'existe pas
        final Long userId = 999L;

        try (MockedStatic<SecurityTools> mockedSecurityTools = Mockito.mockStatic(SecurityTools.class)) {
            CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
            when(mockUserDetails.getId()).thenReturn(userId);
            mockedSecurityTools.when(SecurityTools::getConnectedUser).thenReturn(mockUserDetails);

            // Simuler une exception lorsque l'utilisateur n'est pas trouvé
            when(customerService.getBalanceById(userId))
                    .thenThrow(new NotFoundCustomerException("L'utilisateur est introuvable avec cet ID : " + userId));

            // When & Then
            assertThrows(NotFoundCustomerException.class, () -> transfertController.transfert(model));
        }
    }



        // TODO : A terminer - tests unitaires de la méthode post de transfertController


}
