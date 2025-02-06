package com.ocP6.PayMyBuddy;


import com.ocP6.PayMyBuddy.exception.ConflictConnectionException;
import com.ocP6.PayMyBuddy.service.CustomerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.*;



@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
public class ConnectionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceImpl customerService;


    private static final String URI_PATH = "/connection";


    // NOTE : data.sql for tests datas


    @Test
    @WithUserDetails("user@user.com")
    void connection_shouldReturnConnectionView_whenUserIsAuthenticated() throws Exception {

        // Given user data in data.sql and user is authenticated

        // When making a valid request to the connection page
        ResultActions response = mockMvc.perform(get(URI_PATH).with(csrf()));

        // Then the request should be successful and return the correct view with an empty ConnectionRequest model
        response.andExpect(status().isOk())
                .andExpect(view().name("connection"))
                .andExpect(model().attributeExists("connectionRequest"));

    }



    @Test
    void connection_shouldRedirectToLogin_whenUserIsNotAuthenticated() throws Exception {

        // When making a request without authentication
        ResultActions response = mockMvc.perform(get(URI_PATH).with(csrf()));

        // Then the request should result in a redirect to the login page
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }



    @Test
    @WithUserDetails("user@user.com")
    void addConnection_shouldRedirectToTransfer_whenConnectionIsSuccessful() throws Exception {

        // Given an authenticated user and a valid connection request
        final String email = "copain@copain.com";

        // When posting the connection request
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .param("email", email)
                .with(csrf()));

        // Then the request should succeed, invoking the service, and redirecting to transfer page
        verify(customerService, times(1)).addConnection(1L, email);
        response.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert?connection=true"));

    }



    @Test
    @WithUserDetails("user@user.com")
    void addConnection_shouldReturnConnectionViewWithError_whenValidationFails() throws Exception {

        // Given an authenticated user and an invalid email format
        final String invalidEmail = "invalid-email";

        // When posting an invalid request
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .param("email", invalidEmail)
                .with(csrf()));

        // Then validation should fail and return to the connection page with an error message
        response.andExpect(status().isOk())
                .andExpect(view().name("connection"))
                .andExpect(model().attributeExists("errorMessages"))
                .andExpect(model().attribute("errorMessages", not(empty())))
                .andExpect(model().attribute("errorMessage", not(empty())))
                .andExpect(model().attributeExists("connectionRequest"));

    }



    @Test
    @WithUserDetails("user@user.com")
    void addConnection_shouldReturnConnectionViewWithError_whenServiceThrowsException() throws Exception {

        // Given an authenticated user and a request to add an already connected friend
        final String existingConnection = "friend@friend.com";
        doThrow(new ConflictConnectionException())
                .when(customerService).addConnection(anyLong(), eq(existingConnection));

        // When posting a request with an existing connection
        ResultActions response = mockMvc.perform(post(URI_PATH)
                .param("email", existingConnection)
                .with(csrf()));

        // Then it should return to the connection page with an error message
        response.andExpect(status().isOk())
                .andExpect(view().name("connection"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessages", not(empty())))
                .andExpect(model().attribute("errorMessage", not(empty())))
                .andExpect(model().attributeExists("connectionRequest"));

    }

}
