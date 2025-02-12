package com.ocP6.PayMyBuddy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String URI_PATH = "/";


    // NOTE : data.sql for tests datas


    @Test
    void loginRoot_shouldRedirectToLogin() throws Exception {

        // Given uriPath

        // When making a request to root URL
        ResultActions response = mockMvc.perform(get(URI_PATH));

        // Then the request should result in a redirection to login page
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    void login_shouldReturnLoginView() throws Exception {

        // Given uriPath

        // When making a request to /login
        ResultActions response = mockMvc.perform(get("/login"));

        // Then the request should be successful and return the login view
        response.andExpect(status().isOk())
                .andExpect(view().name("login"));

    }



    @Test
    void login_withValidCredentials_shouldRedirectToTransfert() throws Exception {
        // When submitting valid login credentials
        ResultActions response = mockMvc.perform(post("/login")
                .param("email", "user@user.com")
                .param("password", "user")
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // Then the user should be redirected to the transfert page
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"));
    }

}

