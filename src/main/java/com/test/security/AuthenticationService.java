package com.test.security;

import com.test.model.User;
import com.test.model.enums.UserRole;
import com.test.requests.JwtAuthenticationResponse;
import com.test.requests.SignInRequest;
import com.test.requests.SignUpRequest;
import com.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Username and Password must not be null");
        }

        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JwtAuthenticationResponse("Username is already taken")).getBody();
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .bio(request.getBio())
                .createdAt(LocalDateTime.now())
                .userRole(UserRole.USER)
                .build();

        userService.addUser(user);


        if(userService.existsByUsername(request.getUsername())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            var jwt = jwtService.generateToken(userDetails);
            return new JwtAuthenticationResponse(jwt);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JwtAuthenticationResponse("Error saving user")).getBody();
        }
    }


    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        var jwt = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
}