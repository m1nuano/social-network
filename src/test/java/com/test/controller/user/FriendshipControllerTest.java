package com.test.controller.user;

import com.test.database.dto.FriendshipDto;
import com.test.database.model.Friendship;
import com.test.database.model.User;
import com.test.database.model.enums.FriendshipStatus;
import com.test.service.FriendshipService;
import com.test.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FriendshipControllerTest {

    @Mock
    private FriendshipService friendshipService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendshipController friendshipController;

    private Friendship friendship;
    private FriendshipDto friendshipDto;
    private User currentUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);

        otherUser = new User();
        otherUser.setId(2L);

        friendship = new Friendship();
        friendship.setId(100L);
        friendship.setSender(currentUser);
        friendship.setReceiver(otherUser);
        friendship.setStatus(FriendshipStatus.PENDING);

        friendshipDto = FriendshipDto.builder()
                                     .id(100L)
                                     .status(FriendshipStatus.PENDING)
                                     .build();
    }

    @Test
    void testGetFriendshipsForCurrentUser() {
        List<Friendship> friendships = Arrays.asList(friendship);
        Mockito.when(friendshipService.getFriendshipsForCurrentUser()).thenReturn(friendships);

        Mockito.when(friendshipService.toDTO(ArgumentMatchers.any(Friendship.class))).thenReturn(friendshipDto);

        ResponseEntity<List<FriendshipDto>> response = friendshipController.getFriendshipsForCurrentUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(friendshipDto, response.getBody().get(0));
    }

    @Test
    void testGetPendingOrBlockedRequests() {

        List<Friendship> requests = Arrays.asList(friendship);
        Mockito.when(friendshipService.getPendingOrBlockedRequestsForCurrentUser(FriendshipStatus.PENDING)).thenReturn(requests);
        Mockito.when(friendshipService.toDTO(ArgumentMatchers.any(Friendship.class))).thenReturn(friendshipDto);

        ResponseEntity<List<FriendshipDto>> response = friendshipController.getPendingOrBlockedRequests(FriendshipStatus.PENDING);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(friendshipDto, response.getBody().get(0));
    }

    @Test
    void testSendFriendRequest() {
        Long receiverId = 2L;

        Mockito.when(friendshipService.userSendFriendRequest(receiverId)).thenReturn(friendship);
        Mockito.when(friendshipService.toDTO(friendship)).thenReturn(friendshipDto);

        ResponseEntity<FriendshipDto> response = friendshipController.sendFriendRequest(receiverId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(friendshipDto, response.getBody());
    }

    @Test
    void testUpdateFriendRequestStatusInvalidStatus() {
        ResponseEntity<FriendshipDto> response = friendshipController.updateFriendRequestStatus(100L, FriendshipStatus.PENDING);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateFriendRequestStatusForbidden() {
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(friendshipService.getFriendshipById(100L)).thenReturn(friendship);

        ResponseEntity<FriendshipDto> response = friendshipController.updateFriendRequestStatus(100L, FriendshipStatus.ACCEPTED);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateFriendRequestStatusSuccess() {
        friendship.setReceiver(currentUser);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(friendshipService.getFriendshipById(100L)).thenReturn(friendship);

        Friendship updatedFriendship = new Friendship();
        updatedFriendship.setId(100L);
        updatedFriendship.setSender(currentUser);
        updatedFriendship.setReceiver(currentUser);
        updatedFriendship.setStatus(FriendshipStatus.ACCEPTED);

        Mockito.when(friendshipService.updateFriendshipStatus(100L, FriendshipStatus.ACCEPTED)).thenReturn(updatedFriendship);
        Mockito.when(friendshipService.toDTO(updatedFriendship)).thenReturn(
                FriendshipDto.builder().id(100L).status(FriendshipStatus.ACCEPTED).build()
        );

        ResponseEntity<FriendshipDto> response = friendshipController.updateFriendRequestStatus(100L, FriendshipStatus.ACCEPTED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FriendshipStatus.ACCEPTED, response.getBody().getStatus());
    }

    @Test
    void testDeleteFriendshipForbidden() {
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        Friendship forbiddenFriendship = new Friendship();
        User sender = new User();
        sender.setId(3L);
        User receiver = new User();
        receiver.setId(4L);
        forbiddenFriendship.setSender(sender);
        forbiddenFriendship.setReceiver(receiver);

        Mockito.when(friendshipService.getFriendshipById(200L)).thenReturn(forbiddenFriendship);

        ResponseEntity<Void> response = friendshipController.deleteFriendship(200L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeleteFriendshipSuccess() {
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        Mockito.when(friendshipService.getFriendshipById(100L)).thenReturn(friendship);

        ResponseEntity<Void> response = friendshipController.deleteFriendship(100L);

        Mockito.verify(friendshipService).deleteFriendship(100L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
