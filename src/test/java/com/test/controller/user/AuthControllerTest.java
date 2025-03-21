package com.test.controller.user;

import com.test.database.requests.SignInRequest;
import com.test.database.requests.SignUpRequest;
import com.test.security.AuthenticationService;
import com.test.security.JwtAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testSignUpSuccess() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setFirstName("Test");
        signUpRequest.setLastName("User");
        signUpRequest.setBio("Test bio");

        JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse("test-jwt-token");

        Mockito.when(authenticationService.signUp(ArgumentMatchers.any(SignUpRequest.class)))
               .thenReturn(expectedResponse);

        JwtAuthenticationResponse response = authController.signUp(signUpRequest);

        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
    }

    @Test
    void testSignInSuccess() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("testuser");
        signInRequest.setPassword("password123");

        JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse("test-jwt-token");

        Mockito.when(authenticationService.signIn(ArgumentMatchers.any(SignInRequest.class)))
               .thenReturn(expectedResponse);

        JwtAuthenticationResponse response = authController.signIn(signInRequest);

        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
    }

    @Test
    void testHandleValidationExceptions() {
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        ObjectError error = new ObjectError("signUpRequest", "Username must not be empty");
        Mockito.when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(error));

        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<String> responseEntity = authController.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().contains("Username must not be empty"));
    }
}

