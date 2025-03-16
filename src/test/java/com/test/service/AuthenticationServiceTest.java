package com.test.service;

import com.test.database.model.User;
import com.test.database.model.enums.UserRole;
import com.test.database.requests.SignInRequest;
import com.test.database.requests.SignUpRequest;
import com.test.security.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    private User user;
    private final String jwtToken = "jwtToken";

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("john.doe");
        signUpRequest.setEmail("test@test.com");
        signUpRequest.setPassword("password");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");
        signUpRequest.setBio("test");

        signInRequest = new SignInRequest();
        signInRequest.setUsername("john.doe");
        signInRequest.setPassword("password");

        user = User.builder()
                .id(1L)
                .username("john.doe")
                .email("test@test.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .bio("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userRole(UserRole.ROLE_USER)
                .build();
    }

    @Test
    void testSignUpMissingFields() {
        SignUpRequest request = new SignUpRequest();
        signUpRequest.setUsername("john.doe");
        signUpRequest.setEmail("test@test.com");
        signUpRequest.setPassword("password");
        signUpRequest.setFirstName(null);
        signUpRequest.setLastName("Doe");
        signUpRequest.setBio("test");

        assertThrows(IllegalArgumentException.class, () -> authenticationService.signUp(request));
    }

    @Test
    void testSignUpUsernameAlreadyTaken() {
        when(userService.existsByUsername(signUpRequest.getUsername())).thenReturn(true);

        JwtAuthenticationResponse response = authenticationService.signUp(signUpRequest);
        assertNotNull(response);
        assertEquals("Username is already taken", response.getToken());
        verify(userService).existsByUsername(signUpRequest.getUsername());
    }

    @Test
    void testSignUpSuccess() {
        when(userService.existsByUsername(signUpRequest.getUsername())).thenReturn(false, true);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userService.addUser(any(User.class))).thenReturn(user);

        when(userDetailsService.loadUserByUsername(signUpRequest.getUsername()))
                .thenReturn(new UserImpl(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        JwtAuthenticationResponse response = authenticationService.signUp(signUpRequest);
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        verify(userService).addUser(any(User.class));
        verify(userDetailsService).loadUserByUsername(signUpRequest.getUsername());
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void testSignInSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername(signInRequest.getUsername()))
                .thenReturn(new UserImpl(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        JwtAuthenticationResponse response = authenticationService.signIn(signInRequest);
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(signInRequest.getUsername());
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void testSignInAuthenticationFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> authenticationService.signIn(signInRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
