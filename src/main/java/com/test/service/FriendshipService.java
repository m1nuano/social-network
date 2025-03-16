package com.test.service;

import com.test.database.dto.FriendshipDto;
import com.test.database.mapper.FriendshipMapper;
import com.test.database.model.Friendship;
import com.test.database.model.User;
import com.test.database.model.enums.FriendshipStatus;
import com.test.database.repository.FriendshipRepository;
import com.test.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipMapper friendshipMapper;
    private final UserService userService;

    public FriendshipDto toDTO(Friendship friendship) {
        return friendshipMapper.toDto(friendship);
    }

    public Friendship toEntity(FriendshipDto friendshipDto) {
        return friendshipMapper.toEntity(friendshipDto);
    }

    @Transactional
    public Friendship adminSendFriendRequest(Long senderId, Long receiverId) {
        log.info("Admin sending friend request from User {} to User {}", senderId, receiverId);

        if (friendshipRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            log.warn("Friend request already exists between User {} and User {}", senderId, receiverId);
            throw new RuntimeException("Friend request already exists.");
        }

        User sender = userRepository.findById(senderId)
                                    .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));
        User receiver = userRepository.findById(receiverId)
                                      .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        Friendship friendship = Friendship.builder()
                                          .sender(sender)
                                          .receiver(receiver)
                                          .status(FriendshipStatus.PENDING)
                                          .createdAt(LocalDateTime.now())
                                          .build();

        Friendship savedFriendship = friendshipRepository.save(friendship);
        log.info("Admin: Friend request from User {} to User {} successfully sent", senderId, receiverId);
        return savedFriendship;
    }

    @Transactional
    public Friendship userSendFriendRequest(Long receiverId) {
        User currentUser = userService.getCurrentUser();
        Long senderId = currentUser.getId();
        log.info("User {} sending friend request to User {}", senderId, receiverId);

        if (friendshipRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            log.warn("Friend request already exists between User {} and User {}", senderId, receiverId);
            throw new RuntimeException("Friend request already exists.");
        }

        User receiver = userRepository.findById(receiverId)
                                      .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        Friendship friendship = Friendship.builder()
                                          .sender(currentUser)
                                          .receiver(receiver)
                                          .status(FriendshipStatus.PENDING)
                                          .createdAt(LocalDateTime.now())
                                          .build();

        Friendship savedFriendship = friendshipRepository.save(friendship);
        log.info("User {}: Friend request to User {} successfully sent", senderId, receiverId);
        return savedFriendship;
    }

    @Transactional
    public Friendship updateFriendRequestStatus(Long friendshipId, FriendshipStatus status) {
        log.info("Updating friendship ID {} to status {}", friendshipId, status);

        Friendship friendship = friendshipRepository.findById(friendshipId)
                                                    .orElseThrow(() -> new RuntimeException("Friendship not found with ID: " + friendshipId));

        friendship.setStatus(status);
        Friendship updatedFriendship = friendshipRepository.save(friendship);

        log.info("Friendship ID {} updated to status {}", friendshipId, status);
        return updatedFriendship;
    }

    @Transactional
    public boolean deleteFriendship(Long friendshipId) {
        log.info("Deleting friendship ID {}", friendshipId);

        if (!friendshipRepository.existsById(friendshipId)) {
            log.warn("Friendship not found with ID {}", friendshipId);
            throw new RuntimeException("Friendship not found with ID: " + friendshipId);
        }

        friendshipRepository.deleteById(friendshipId);
        log.info("Friendship ID {} successfully deleted", friendshipId);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Friendship> getFriendshipsForUser(Long userId) {
        log.info("Fetching all friendships for User {}", userId);
        return friendshipRepository.findAllBySenderIdOrReceiverId(userId, userId);
    }

    @Transactional(readOnly = true)
    public List<Friendship> getFriendshipsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        log.info("Fetching accepted friendships for user {}", userId);
        return friendshipRepository.findAllBySenderIdOrReceiverIdAndStatus(userId, userId, FriendshipStatus.ACCEPTED);
    }

    @Transactional
    public Friendship updateFriendshipStatus(Long friendshipId, FriendshipStatus status) {
        log.info("Updating friendship ID {} to status {}", friendshipId, status);
        Friendship friendship = friendshipRepository.findById(friendshipId)
                                                    .orElseThrow(() -> new RuntimeException("Friendship not found with ID: " + friendshipId));
        friendship.setStatus(status);
        Friendship updatedFriendship = friendshipRepository.save(friendship);
        log.info("Friendship ID {} updated to status {}", friendshipId, status);
        return updatedFriendship;
    }

    @Transactional(readOnly = true)
    public Friendship getFriendshipById(Long friendshipId) {
        log.info("Fetching friendship with ID: {}", friendshipId);
        return friendshipRepository.findById(friendshipId)
                                   .orElseThrow(() -> new RuntimeException("Friendship not found with ID: " + friendshipId));
    }

    @Transactional(readOnly = true)
    public List<Friendship> getPendingOrBlockedRequestsForCurrentUser(FriendshipStatus status) {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        log.info("Fetching {} friend requests for user {}", status, userId);

        if (status != FriendshipStatus.PENDING && status != FriendshipStatus.BLOCKED) {
            throw new IllegalArgumentException("Invalid status. Allowed values: PENDING, BLOCKED.");
        }

        return friendshipRepository.findAllByReceiverIdAndStatus(userId, status);
    }
}
