package com.test.service;

import com.test.database.model.User;
import com.test.database.model.enums.UserRole;
import com.test.database.repository.UserRepository;
import com.test.security.JwtService;
import com.test.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    private final String testSigningKey = "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", testSigningKey);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void testGenerateTokenAndExtractUserName() {
        User user = User.builder()
                        .id(1L)
                        .username("testuser")
                        .userRole(UserRole.ROLE_USER)
                        .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token, "Token should not be null");

        String extractedUsername = jwtService.extractUserName(token);
        assertEquals("testuser", extractedUsername, "Extracted username should match");
    }

    @Test
    void testIsTokenValid() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .userRole(UserRole.ROLE_USER)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails), "Token should be valid for the same user");

        User anotherUser = User.builder()
                .id(2L)
                .username("anotheruser")
                .userRole(UserRole.ROLE_USER)
                .build();

        when(userRepository.findByUsername(anotherUser.getUsername())).thenReturn(Optional.of(anotherUser));
        UserDetails anotherUserDetails = userDetailsService.loadUserByUsername(anotherUser.getUsername());
        assertFalse(jwtService.isTokenValid(token, anotherUserDetails), "Token should not be valid for a different user");
    }

    @Test
    void testIsTokenExpired() {
        byte[] keyBytes = Decoders.BASE64.decode(testSigningKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Date issuedAt = new Date(System.currentTimeMillis() - 120_000);
        Date expiration = new Date(System.currentTimeMillis() - 60_000);


        String expiredToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .userRole(UserRole.ROLE_USER)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.isTokenValid(expiredToken, userDetails),
                "Expired token should throw ExpiredJwtException");
    }

}
