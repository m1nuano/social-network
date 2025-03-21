package com.test.service;

import com.test.database.model.Friendship;
import com.test.database.model.User;
import com.test.database.model.enums.FriendshipStatus;
import com.test.database.repository.FriendshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelationshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private RelationshipService relationshipService;

    private User user1;
    private User user2;
    private Friendship friendship;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);

        user2 = new User();
        user2.setId(2L);

        friendship = new Friendship();
        friendship.setId(10L);
        friendship.setSender(user1);
        friendship.setReceiver(user2);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
    }

    @Test
    void testGetFriendshipBetween_Success() {
        when(friendshipRepository.findBySenderIdAndReceiverId(1L, 2L)).thenReturn(Optional.of(friendship));

        Friendship result = relationshipService.getFriendshipBetween(1L, 2L);

        assertNotNull(result);
        assertEquals(friendship.getId(), result.getId());
    }

    @Test
    void testGetFriendshipBetween_ReverseLookup() {
        when(friendshipRepository.findBySenderIdAndReceiverId(1L, 2L)).thenReturn(Optional.empty());
        when(friendshipRepository.findBySenderIdAndReceiverId(2L, 1L)).thenReturn(Optional.of(friendship));

        Friendship result = relationshipService.getFriendshipBetween(1L, 2L);

        assertNotNull(result);
        assertEquals(friendship.getId(), result.getId());
    }

    @Test
    void testGetFriendshipBetween_NotFound() {
        when(friendshipRepository.findBySenderIdAndReceiverId(1L, 2L)).thenReturn(Optional.empty());
        when(friendshipRepository.findBySenderIdAndReceiverId(2L, 1L)).thenReturn(Optional.empty());

        Friendship result = relationshipService.getFriendshipBetween(1L, 2L);

        assertNull(result);
    }

    @Test
    void testIsBlocked_UserIsBlocked() {
        friendship.setStatus(FriendshipStatus.BLOCKED);

        when(friendshipRepository.findBySenderIdAndReceiverId(1L, 2L)).thenReturn(Optional.of(friendship));

        boolean result = relationshipService.isBlocked(1L, 2L);

        assertTrue(result);
    }

    @Test
    void testIsBlocked_UserIsNotBlocked() {
        when(friendshipRepository.findBySenderIdAndReceiverId(1L, 2L)).thenReturn(Optional.of(friendship));

        boolean result = relationshipService.isBlocked(1L, 2L);

        assertFalse(result);
    }

    @Test
    void testIsBlocked_NoFriendship() {
        when(friendshipRepository.findBySenderIdAndReceiverId(1L, 2L)).thenReturn(Optional.empty());
        when(friendshipRepository.findBySenderIdAndReceiverId(2L, 1L)).thenReturn(Optional.empty());

        boolean result = relationshipService.isBlocked(1L, 2L);

        assertFalse(result);
    }
}
