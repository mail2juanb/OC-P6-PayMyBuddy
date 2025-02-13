package com.ocP6.PayMyBuddy;


import com.ocP6.PayMyBuddy.dto.RegisterRequest;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
public class RegisterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerServiceImpl customerService;


    private static final String URI_PATH = "/register";


    // NOTE : data.sql for tests datas


    @Test
    void register_shouldDisplayRegisterView_whenAccessingRegisterPage() throws Exception {

        // Given no preconditions

        // When accessing the register page
        ResultActions response = mockMvc.perform(get(URI_PATH));

        // Then the page should be displayed successfully with the correct model attributes
        response.andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));

    }



    @Test
    void register_shouldRegisterCustomerSuccessfully_whenValidDataIsProvided() throws Exception {

        // Given a valid registration request
        RegisterRequest request = new RegisterRequest("newUser", "newuser@example.com", "password123");

        // When sending a POST request to /register with valid user data
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .param("username", request.getUsername())
                .param("email", request.getEmail())
                .param("password", request.getPassword()));

        // Then the user should be redirected to the login page with a success message
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));

    }



    @Test
    void register_shouldFailRegistration_whenFieldsAreInvalid() throws Exception {

        // Given invalid registration data

        // When submitting a registration request with invalid fields
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .param("username", "")
                .param("email", "invalid-email")
                .param("password", ""));

        // Then an error message should be displayed on the registration page
        response.andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessages"))
                .andExpect(model().attribute("errorMessages", not(empty())));

    }



    // Returns invalid arguments
    static Stream<Arguments> provideInvalidArguments() {
        return Stream.of(
                Arguments.of("friend", "newEmail@test.com", "newPassword"),
                Arguments.of("newUsername", "friend@friend.com", "newPassword"),
                Arguments.of("  ", "invalid-email", "")
        );

    }

    @ParameterizedTest
    @MethodSource("provideInvalidArguments")
    void register_shouldFailRegistration_withInvalidArguments(String username, String email, String password) throws Exception {

        // Given a request
        final String request = "{ \"username\" : \"" + username + "\" , \"email\": \"" +  email + "\" , \"password\": \""+  password +  "\"}";

        // When trying to register with an already taken username
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .with(csrf())
                .contentType("application/json")
                .content(request)
        );

        // Then an error message should be displayed
        response.andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessages"))
                .andExpect(model().attribute("errorMessages", not(empty())));

    }



    @Test
    void register_shouldFailRegistration_whenDatabaseConstraintFails() throws Exception {

        // Given a request that will trigger a database constraint violation (email already in use)
        RegisterRequest request = new RegisterRequest("existingUser", "existing@example.com", "password123");

        // Insert an existing user
        customerService.createCustomer("existingUser", "existing@example.com", "password123");

        // When attempting to register a new user with the same email
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .param("username", request.getUsername())
                .param("email", request.getEmail())
                .param("password", request.getPassword()));

        // Then the user should stay on the registration page with an error message
        response.andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", not(empty())));
    }


}
