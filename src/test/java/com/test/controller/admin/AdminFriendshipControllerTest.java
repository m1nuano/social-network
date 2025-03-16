package com.test.controller.admin;

import com.test.database.dto.FriendshipDto;
import com.test.database.model.Friendship;
import com.test.database.model.enums.FriendshipStatus;
import com.test.service.FriendshipService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFriendshipControllerTest {

    @Mock
    private FriendshipService friendshipService;

    @InjectMocks
    private AdminFriendshipController adminFriendshipController;

    private Friendship friendship;
    private FriendshipDto friendshipDto;

    @BeforeEach
    void setUp() {
        friendship = new Friendship();
        friendship.setId(1L);
        friendship.setStatus(FriendshipStatus.PENDING);

        friendshipDto = FriendshipDto.builder()
                                     .id(1L)
                                     .status(FriendshipStatus.PENDING)
                                     .build();
    }

    @Test
    void testGetFriendshipsForUser() {
        List<Friendship> friendships = List.of(friendship);

        when(friendshipService.getFriendshipsForUser(1L)).thenReturn(friendships);
        when(friendshipService.toDTO(friendship)).thenReturn(friendshipDto);

        ResponseEntity<List<FriendshipDto>> response = adminFriendshipController.getFriendshipsForUser(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(FriendshipStatus.PENDING, response.getBody().get(0).getStatus());
    }

    @Test
    void testSendFriendRequest() {
        when(friendshipService.adminSendFriendRequest(1L, 2L)).thenReturn(friendship);
        when(friendshipService.toDTO(friendship)).thenReturn(friendshipDto);

        ResponseEntity<FriendshipDto> response = adminFriendshipController.sendFriendRequest(1L, 2L);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(FriendshipStatus.PENDING, response.getBody().getStatus());
    }

    @Test
    void testUpdateFriendRequestStatus() {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipDto.setStatus(FriendshipStatus.ACCEPTED);

        when(friendshipService.updateFriendRequestStatus(1L, FriendshipStatus.ACCEPTED)).thenReturn(friendship);
        when(friendshipService.toDTO(friendship)).thenReturn(friendshipDto);

        ResponseEntity<FriendshipDto> response = adminFriendshipController.updateFriendRequestStatus(1L, FriendshipStatus.ACCEPTED);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(FriendshipStatus.ACCEPTED, response.getBody().getStatus());
    }

    @Test
    void testDeleteFriendship_Success() {
        when(friendshipService.deleteFriendship(1L)).thenReturn(true);

        ResponseEntity<Void> response = adminFriendshipController.deleteFriendship(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(friendshipService, times(1)).deleteFriendship(eq(1L));
    }

    @Test
    void testDeleteFriendship_NotFound() {
        when(friendshipService.deleteFriendship(1L)).thenReturn(false);

        ResponseEntity<Void> response = adminFriendshipController.deleteFriendship(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
