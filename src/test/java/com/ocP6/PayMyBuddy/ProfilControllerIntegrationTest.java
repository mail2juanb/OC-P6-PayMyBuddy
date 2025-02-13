package com.ocP6.PayMyBuddy;

import com.ocP6.PayMyBuddy.dto.CreditBalanceRequest;
import com.ocP6.PayMyBuddy.dto.ProfilRequest;
import com.ocP6.PayMyBuddy.service.CustomerServiceImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;




@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
public class ProfilControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerServiceImpl customerService;


    private static final String URI_PATH = "/profil";
    private static final String CREDIT_BALANCE_PATH = "/creditBalance";


    // NOTE : data.sql for tests datas


    @Test
    @WithUserDetails("user@user.com")
    void profil_shouldReturnProfilViewWithUserDetails_whenUserIsAuthenticated() throws Exception {
        // Given user data in data.sql

        // When making a valid request to the URL
        ResultActions response = mockMvc.perform(get(URI_PATH).with(csrf()));

        // Then the request should be successful, and the correct view and model attributes should be returned
        response.andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("username", "email", "profilRequest"))
                .andExpect(model().attribute("username", "user"))
                .andExpect(model().attribute("email", "user@user.com"));

    }



    @Test
    void profil_shouldRedirectToLogin_whenUserIsNotAuthenticated() throws Exception {
        // When making a request without authentication
        ResultActions response = mockMvc.perform(get(URI_PATH).with(csrf()));

        // Then the request should result in a redirect to the login page
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }



    @Test
    @WithUserDetails("user@user.com")
    void updateProfil_shouldRedirectToTransfert_whenUpdateIsSuccessful() throws Exception {

        // Given user data in data.sql
        ProfilRequest request = new ProfilRequest("newUsername", "newEmail@test.com", "newPassword");

        // When sending a POST request to /profil
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .with(csrf())
                .param("username", request.getUsername())
                .param("email", request.getEmail())
                .param("password", request.getPassword()));

        // Then the response should be a redirect to /transfert?profil=true
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert?profil=true"));

    }



    // Returns invalid arguments
    static Stream<Arguments> provideInvalidArguments() {
        return Stream.of(
                Arguments.of( "friend", "newEmail@test.com", "newPassword"),
                Arguments.of( "newUsername", "friend@friend.com", "newPassword"),
                Arguments.of( " ", "test@test.com", "newPassword"),
                Arguments.of( "invalid", "", "newPassword"),
                Arguments.of( "invalid", "test@test.com", ""),
                Arguments.of( "invalid", "invalid-email", "newPassword")
        );
    }


    @ParameterizedTest
    @MethodSource("provideInvalidArguments")
    @WithUserDetails("user@user.com")
    void updateProfil_shouldReturnError_withInvalidArguments (String username, String email, String password) throws Exception {

        // Given a request
        final String request = "{ \"username\" : \"" + username + "\" , \"email\": \"" +  email + "\" , \"password\": \""+  password +  "\"}";

        // When sending a POST request to /profil
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .with(csrf())
                .contentType("application/json")
                .content(request)
        );

        // Then the response should display an error message and stay on the same page
        response.andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("errorMessages"))
                .andExpect(model().attribute("errorMessages", not(empty())))
                .andExpect(model().attribute("username", "user"))
                .andExpect(model().attribute("email", "user@user.com"));

    }



    @Test
    @WithUserDetails("user@user.com")
    void updateProfil_shouldReturnProfilView_whenUpdateFails() throws Exception {

        // Given: An email that already exists in the database
        customerService.createCustomer("anotherUser", "existing@email.com", "password123");

        ProfilRequest request = new ProfilRequest("validUsername", "existing@email.com", "validPassword");

        // When: Sending a POST request to /profil with a duplicate email
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .with(csrf())
                .param("username", request.getUsername())
                .param("email", request.getEmail())
                .param("password", request.getPassword()));

        // Then: The response should return the "profil" view with an error message
        response.andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", not(empty())));

    }



    @Test
    @WithUserDetails("user@user.com")
    void creditBalance_shouldRedirectToTransfert_whenCreditIsSuccessful() throws Exception {

        // Given user data in data.sql
        final BigDecimal customerBalance = customerService.getBalanceById(1L);
        final BigDecimal creditAmount = BigDecimal.valueOf(50);
        final CreditBalanceRequest request = new CreditBalanceRequest(BigDecimal.valueOf(50));

        // When make a valid query on the url
        ResultActions response = mockMvc.perform(post(CREDIT_BALANCE_PATH)
                .with(csrf())
                .param("balance", String.valueOf(request.getBalance()))
        );

        // Then: The response should redirect to /transfert?balanceSuccess=true
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert?balanceSuccess=true"));
        final BigDecimal newCustomerBalance = customerService.getBalanceById(1L);
        assertThat(newCustomerBalance).isEqualTo(customerBalance.add(creditAmount));
    }



    // Returns invalid arguments
    static Stream<Arguments> provideInvalidCredits() {
        return Stream.of(
                Arguments.of( BigDecimal.valueOf(0)),
                Arguments.of( BigDecimal.valueOf(-1))
        );
    }


    @ParameterizedTest
    @MethodSource("provideInvalidCredits")
    @WithUserDetails("user@user.com")
    void creditBalance_shouldReturnProfilView_withInvalidAmount(BigDecimal balance) throws Exception {

        // Given: An invalid credit balance request (amount <= 0)

        // When: Sending a POST request to /creditBalance
        ResultActions response = mockMvc.perform(post(CREDIT_BALANCE_PATH)
                .with(csrf())
                .content(String.valueOf(balance))
        );

        // Then: The response should return the profil view with an error message
        response.andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("errorMessages"))
                .andExpect(model().attribute("errorMessages", not(empty())));
    }



    @Test
    void creditBalance_shouldRedirectToLogin_whenUserIsNotAuthenticated() throws Exception {

        // Given a credit request without authentication
        final CreditBalanceRequest request = new CreditBalanceRequest(BigDecimal.valueOf(99));

        // When sending a POST request to /creditBalance
        ResultActions response = mockMvc.perform(post(CREDIT_BALANCE_PATH)
                .with(csrf())
                .param("balance", String.valueOf(request.getBalance()))
        );

        // Then tThe response should redirect to the login page
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

}
