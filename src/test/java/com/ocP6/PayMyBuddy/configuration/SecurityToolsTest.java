package com.ocP6.PayMyBuddy.configuration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;


@ExtendWith(MockitoExtension.class)
public class SecurityToolsTest {

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private SecurityContext securityContext;

    @Test
    void getConnectedUser_shouldReturnUser_whenAuthenticated() {

        // Given a valid authentication with a CustomUserDetails object
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, List.of());

        // When the security context is mocked to return the authentication, and we call getConnectedUser
        try (var securityContextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            CustomUserDetails result = SecurityTools.getConnectedUser();

            // Then the result should not be null and should match the CustomUserDetails
            assertNotNull(result);
            assertEquals(customUserDetails, result);
        }

    }



    @Test
    void getConnectedUser_shouldThrowException_whenNotAuthenticated() {

        // When the security context is mocked to return null for authentication, and we call getConnectedUser
        try (MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
            when(securityContext.getAuthentication()).thenReturn(null);
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Then a BadCredentialsException should be thrown because the user is not authenticated
            assertThrows(BadCredentialsException.class, SecurityTools::getConnectedUser);
        }

    }



    @Test
    void getConnectedUser_shouldThrowException_whenAuthenticationIsNotAuthenticated() {

        // Given the security context returns an unauthenticated authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        try (var securityContextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When // Then calling getConnectedUser should throw BadCredentialsException
            assertThrows(BadCredentialsException.class, SecurityTools::getConnectedUser);
        }

    }



    @Test
    void getConnectedUser_shouldThrowException_whenPrincipalIsNotCustomUserDetails() {

        // Given the security context returns an authentication with a non-CustomUserDetails principal
        Authentication authentication = new UsernamePasswordAuthenticationToken("invalidPrincipal", null, List.of());

        try (var securityContextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When // Then calling getConnectedUser should throw BadCredentialsException
            assertThrows(BadCredentialsException.class, SecurityTools::getConnectedUser);
        }

    }

}
