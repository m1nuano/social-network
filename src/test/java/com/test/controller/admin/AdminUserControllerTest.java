package com.test.controller.admin;

import com.test.database.dto.UserDto;
import com.test.database.model.User;
import com.test.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserController adminUserController;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                   .id(1L)
                   .username("Test User")
                   .email("testuser@example.com")
                   .build();

        userDto = UserDto.builder()
                         .id(1L)
                         .username("Test User")
                         .email("testuser@example.com")
                         .build();
    }

    @Test
    void testListUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userService.toDTO(any(User.class))).thenReturn(userDto);

        ResponseEntity<List<UserDto>> response = adminUserController.listUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test User", response.getBody().get(0).getUsername());
    }

    @Test
    void testGetUser() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.toDTO(user)).thenReturn(userDto);

        ResponseEntity<UserDto> response = adminUserController.getUser(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test User", response.getBody().getUsername());
    }

    @Test
    void testCreateUser() {
        when(userService.toEntity(any(UserDto.class))).thenReturn(user);
        when(userService.addUser(any(User.class))).thenReturn(user);
        when(userService.toDTO(any(User.class))).thenReturn(userDto);

        ResponseEntity<UserDto> response = adminUserController.createUser(userDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test User", response.getBody().getUsername());
    }

    @Test
    void testUpdateUser() {
        when(userService.toEntity(any(UserDto.class))).thenReturn(user);
        when(userService.updateUser(any(User.class))).thenReturn(user);
        when(userService.toDTO(any(User.class))).thenReturn(userDto);

        ResponseEntity<UserDto> response = adminUserController.updateUser(1L, userDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test User", response.getBody().getUsername());
    }

    @Test
    void testDeleteUser_Success() {
        when(userService.deleteUser(1L)).thenReturn(true);

        ResponseEntity<Void> response = adminUserController.deleteUser(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(eq(1L));
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userService.deleteUser(1L)).thenReturn(false);

        ResponseEntity<Void> response = adminUserController.deleteUser(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
