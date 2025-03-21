package com.test.service;

import com.test.database.dto.FriendshipDto;
import com.test.database.mapper.FriendshipMapper;
import com.test.database.model.Friendship;
import com.test.database.model.User;
import com.test.database.model.enums.FriendshipStatus;
import com.test.database.repository.FriendshipRepository;
import com.test.database.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipMapper friendshipMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendshipService friendshipService;

    private User sender;
    private User receiver;
    private Friendship friendship;
    private FriendshipDto friendshipDto;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setUsername("sender");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiver");

        friendship = Friendship.builder()
                               .sender(sender)
                               .receiver(receiver)
                               .status(FriendshipStatus.PENDING)
                               .createdAt(LocalDateTime.now())
                               .build();
        friendship.setId(100L);

        friendshipDto = new FriendshipDto();
        friendshipDto.setId(100L);
    }

    @Test
    void testToDTO() {
        when(friendshipMapper.toDto(friendship)).thenReturn(friendshipDto);
        FriendshipDto result = friendshipService.toDTO(friendship);
        assertNotNull(result);
        assertEquals(friendshipDto.getId(), result.getId());
        verify(friendshipMapper).toDto(friendship);
    }

    @Test
    void testToEntity() {
        when(friendshipMapper.toEntity(friendshipDto)).thenReturn(friendship);
        Friendship result = friendshipService.toEntity(friendshipDto);
        assertNotNull(result);
        assertEquals(friendship.getId(), result.getId());
        verify(friendshipMapper).toEntity(friendshipDto);
    }

    @Test
    void testAdminSendFriendRequest_Success() {
        when(friendshipRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())).thenReturn(false);
        when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(friendshipRepository.save(any(Friendship.class))).thenAnswer(invocation -> {
            Friendship saved = invocation.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        Friendship result = friendshipService.adminSendFriendRequest(sender.getId(), receiver.getId());
        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals(FriendshipStatus.PENDING, result.getStatus());
        verify(friendshipRepository).existsBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        verify(userRepository).findById(sender.getId());
        verify(userRepository).findById(receiver.getId());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void testAdminSendFriendRequest_AlreadyExists() {
        when(friendshipRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendshipService.adminSendFriendRequest(sender.getId(), receiver.getId()));
        assertTrue(exception.getMessage().contains("Friend request already exists"));
        verify(friendshipRepository).existsBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void testAdminSendFriendRequest_SenderNotFound() {
        when(friendshipRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())).thenReturn(false);
        when(userRepository.findById(sender.getId())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendshipService.adminSendFriendRequest(sender.getId(), receiver.getId()));
        assertTrue(exception.getMessage().contains("Sender not found with ID"));
        verify(userRepository).findById(sender.getId());
        verify(userRepository, never()).findById(receiver.getId());
    }

    @Test
    void testAdminSendFriendRequest_ReceiverNotFound() {
        when(friendshipRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())).thenReturn(false);
        when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendshipService.adminSendFriendRequest(sender.getId(), receiver.getId()));
        assertTrue(exception.getMessage().contains("Receiver not found with ID"));
        verify(userRepository).findById(sender.getId());
        verify(userRepository).findById(receiver.getId());
    }

    @Test
    void testUserSendFriendRequest_Success() {
        when(userService.getCurrentUser()).thenReturn(sender);

        when(friendshipRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())).thenReturn(false);
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(friendshipRepository.save(any(Friendship.class))).thenAnswer(invocation -> {
            Friendship saved = invocation.getArgument(0);
            saved.setId(300L);
            return saved;
        });

        Friendship result = friendshipService.userSendFriendRequest(receiver.getId());
        assertNotNull(result);
        assertEquals(300L, result.getId());
        verify(userService).getCurrentUser();
        verify(friendshipRepository).existsBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        verify(userRepository).findById(receiver.getId());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void testUserSendFriendRequest_AlreadyExists() {
        when(userService.getCurrentUser()).thenReturn(sender);
        when(friendshipRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendshipService.userSendFriendRequest(receiver.getId()));
        assertTrue(exception.getMessage().contains("Friend request already exists"));
        verify(userService).getCurrentUser();
        verify(friendshipRepository).existsBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void testUpdateFriendRequestStatus_Success() {
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(friendship)).thenReturn(friendship);

        Friendship result = friendshipService.updateFriendRequestStatus(friendship.getId(), FriendshipStatus.ACCEPTED);
        assertNotNull(result);
        assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());
        verify(friendshipRepository).findById(friendship.getId());
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void testUpdateFriendRequestStatus_NotFound() {
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendshipService.updateFriendRequestStatus(friendship.getId(), FriendshipStatus.ACCEPTED));
        assertTrue(exception.getMessage().contains("Friendship not found with ID"));
        verify(friendshipRepository).findById(friendship.getId());
    }

    @Test
    void testDeleteFriendship_NotFound() {
        when(friendshipRepository.existsById(friendship.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendshipService.deleteFriendship(friendship.getId()));
        assertTrue(exception.getMessage().contains("Friendship not found with ID"));
        verify(friendshipRepository).existsById(friendship.getId());
        verify(friendshipRepository, never()).deleteById(friendship.getId());
    }

    @Test
    void testGetFriendshipsForUser() {
        Long userId = sender.getId();
        List<Friendship> friendships = Arrays.asList(friendship);
        when(friendshipRepository.findAllBySenderIdOrReceiverId(userId, userId)).thenReturn(friendships);
        List<Friendship> result = friendshipService.getFriendshipsForUser(userId);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(friendshipRepository).findAllBySenderIdOrReceiverId(userId, userId);
    }

    @Test
    void testGetFriendshipsForCurrentUser() {
        when(userService.getCurrentUser()).thenReturn(sender);
        List<Friendship> friendships = Arrays.asList(friendship);
        when(friendshipRepository.findAllBySenderIdOrReceiverIdAndStatus(sender.getId(), sender.getId(), FriendshipStatus.ACCEPTED))
                .thenReturn(friendships);
        List<Friendship> result = friendshipService.getFriendshipsForCurrentUser();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userService).getCurrentUser();
        verify(friendshipRepository)
                .findAllBySenderIdOrReceiverIdAndStatus(sender.getId(), sender.getId(), FriendshipStatus.ACCEPTED);
    }

    @Test
    void testUpdateFriendshipStatus_Success() {
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(friendship)).thenReturn(friendship);
        Friendship result = friendshipService.updateFriendshipStatus(friendship.getId(), FriendshipStatus.BLOCKED);
        assertNotNull(result);
        assertEquals(FriendshipStatus.BLOCKED, result.getStatus());
        verify(friendshipRepository).findById(friendship.getId());
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void testGetFriendshipById_Success() {
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.of(friendship));
        Friendship result = friendshipService.getFriendshipById(friendship.getId());
        assertNotNull(result);
        assertEquals(friendship.getId(), result.getId());
        verify(friendshipRepository).findById(friendship.getId());
    }

    @Test
    void testGetFriendshipById_NotFound() {
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendshipService.getFriendshipById(friendship.getId()));
        assertTrue(exception.getMessage().contains("Friendship not found with ID"));
        verify(friendshipRepository).findById(friendship.getId());
    }

    @Test
    void testGetPendingOrBlockedRequestsForCurrentUser_Success() {
        when(userService.getCurrentUser()).thenReturn(sender);
        List<Friendship> friendships = Arrays.asList(friendship);
        when(friendshipRepository.findAllByReceiverIdAndStatus(sender.getId(), FriendshipStatus.PENDING))
                .thenReturn(friendships);
        List<Friendship> result = friendshipService.getPendingOrBlockedRequestsForCurrentUser(FriendshipStatus.PENDING);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userService).getCurrentUser();
        verify(friendshipRepository).findAllByReceiverIdAndStatus(sender.getId(), FriendshipStatus.PENDING);
    }
}
