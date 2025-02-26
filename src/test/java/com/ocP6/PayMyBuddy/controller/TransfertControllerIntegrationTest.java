package com.ocP6.PayMyBuddy.controller;


import com.ocP6.PayMyBuddy.service.CustomerServiceImpl;
import com.ocP6.PayMyBuddy.service.TransactionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.comparesEqualTo;



@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
public class TransfertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private TransactionServiceImpl transactionService;

    private static final String URI_PATH = "/transfert";


    // NOTE : data.sql for tests datas


    @Test
    @WithUserDetails("user@user.com")
    void transfert_shouldReturnViewTransfert_whenUserIsAuthenticated() throws Exception {

        // Given uriPath

        // When make a valid query on the url
        ResultActions response = mockMvc.perform(get(URI_PATH).with(csrf()));

        // Then the request should be successful, and the correct view and model attributes should be returned
        response.andExpect(status().isOk())                                                                                    // Vérifie que le code HTTP est 200 (OK)
                .andExpect(view().name("transfert"))                                                           // Vérifie que la vue "transfert" est retournée
                .andExpect(model().attributeExists("connections", "transactions", "balance", "transfertRequest"))      // Vérifie l'existence des attributs
                .andExpect(model().attribute("connections", not(empty())))                                               // Vérifie que connections n'est pas vide
                .andExpect(model().attribute("transactions", not(empty())))                                              // Vérifie que transactions n'est pas vide
                .andExpect(model().attribute("balance", comparesEqualTo(BigDecimal.valueOf(5000))));                     // Vérifie que balance a la valeur attendue

    }



    @Test
    void transfert_RedirectToLoginPage_whenUserIsNotAuthenticated() throws Exception {

        // Given uriPath

        // When an unauthenticated user attempts to access the transfer page
        ResultActions response = mockMvc.perform(get(URI_PATH).with(csrf()));

        // Then the user should be redirected to the login page
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }



    @Test
    @WithUserDetails("copain@copain.com")
    void transfert_shouldReturnViewTransfertWithEmptyLists_whenUserHasNoConnectionsOrTransactions() throws Exception {

        // Given an authenticated user with no connections or transactions and the transfer page endpoint

        // When make a query on the url
        ResultActions response = mockMvc.perform(get(URI_PATH).with(csrf()));

        // Then the request should return an empty list for both connections and transactions
        response.andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeExists("connections", "transactions", "balance", "transfertRequest"))
                .andExpect(model().attribute("connections", empty()))
                .andExpect(model().attribute("transactions", empty()))
                .andExpect(model().attribute("balance", comparesEqualTo(BigDecimal.valueOf(100))));

    }



    @Test
    @WithUserDetails("ami@ami.com")
    void transfert_shouldReturnViewTransfertWithZeroBalance_whenUserHasNoMoney() throws Exception {

        // Given an authenticated user with zero balance and the transfer page endpoint

        // When make a query on the url
        ResultActions response = mockMvc.perform(get(URI_PATH).with(csrf()));

        // Then check answer
        response.andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeExists("connections", "transactions", "balance", "transfertRequest"))
                .andExpect(model().attribute("balance", comparesEqualTo(BigDecimal.ZERO)));

    }



    @Test
    @WithUserDetails("user@user.com")
    void transfert_shouldProcessTransactionSuccessfullyAndRedirectToTransfertPage() throws Exception {

        // Given uriPath

        // When make a valid query on the url
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .with(csrf())                                                                   // Ajout du token CSRF pour éviter les erreurs
                .param("relationId", "2")                                         // ID d'un contact valide, envoi vers friend@friend.com (ID=2)
                .param("description", "Remboursement")                          // Une description valide
                .param("amount", "100")                                           // Montant valide et inférieur au solde
        );

        // Then check answer successfull with redirection and success attribute
        response.andExpect(status().is3xxRedirection())                                         // Vérifie qu'on est bien redirigé
                .andExpect(redirectedUrl("/transfert?transaction=true"));           // Vérifie que l'utilisateur est redirigé vers la page de transfert avec un paramètre de succès

    }



    @Test
    @WithUserDetails("ami@ami.com")                                                         // L'utilisateur a un solde insuffisant
    void transfert_shouldFail_whenBalanceIsTooLow() throws Exception {

        // Given uriPath

        // When user tries to send 10€ when he doesn't have any
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .with(csrf())
                .param("relationId", "2")                                   // Envoi vers friend@friend.com (ID=2)
                .param("description", "Paiement trop élevé")
                .param("amount", "10")                                      // Montant trop élevé
        );

        // Then the transfer is refused and an error message is displayed
        response.andExpect(status().isOk())                                                 // Vérifie que la requête ne redirige pas (l'erreur est affichée)
                .andExpect(view().name("transfert"))                        // Vérifie que la même page est renvoyée
                .andExpect(model().attributeExists("errorMessage"))                 // Vérifie que le modèle contient un message d'erreur
                .andExpect(model().attribute("errorMessage", not(empty())));            // Vérifie que le message d'erreur est bien affiché

    }



    // Returns invalid arguments
    static Stream<Arguments> provideInvalidArguments() {
        return Stream.of(
                Arguments.of( 2L, "Montant négatif", BigDecimal.valueOf(-50)),
                Arguments.of( 4L, "Connexion invalide", BigDecimal.valueOf(20)),
                Arguments.of( 1L, "Self-transfer", BigDecimal.valueOf(100)),
                Arguments.of( 2L, "Excessive transfer", BigDecimal.valueOf(10000000)),
                Arguments.of( 4L, "Unconnected transfer", BigDecimal.valueOf(50))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidArguments")
    @WithUserDetails("user@user.com")
    void transfert_shouldFail_withInvalidArguments (Long relationId, String description, BigDecimal amount) throws Exception {

        // Given a request
        final String request = "{ \"relationId\" : \"" + relationId + "\" , \"description\": \"" +  description + "\" , \"amount\": \""+  amount +  "\"}";

        // When user tries to send transaction
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .with(csrf())
                .contentType("application/json")
                .content(request)
        );

        // Then the transfer is refused and an error message is displayed
        response.andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeExists("errorMessages"))
                .andExpect(model().attribute("errorMessages", not(empty())));

    }

}
