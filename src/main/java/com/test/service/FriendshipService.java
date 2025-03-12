package com.test.service;

import com.test.dto.FriendshipDto;
import com.test.mapper.FriendshipMapper;
import com.test.model.Friendship;
import com.test.model.User;
import com.test.model.enums.FriendshipStatus;
import com.test.repository.FriendshipRepository;
import com.test.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipMapper friendshipMapper;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository, FriendshipMapper friendshipMapper) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.friendshipMapper = friendshipMapper;
    }

    public FriendshipDto toDTO(Friendship friendship) {
        return friendshipMapper.toDto(friendship);
    }

    public Friendship toEntity(FriendshipDto friendshipDto) {
        return friendshipMapper.toEntity(friendshipDto);
    }

    @Transactional
    public Friendship sendFriendRequest(Long senderId, Long receiverId) {
        log.info("Sending friend request from User {} to User {}", senderId, receiverId);

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
        log.info("Friend request from User {} to User {} successfully sent", senderId, receiverId);
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
        log.info("Fetching friendships for User {}", userId);
        //TODO: userId как заглушка, изменить логику получения друзей юзера
        return friendshipRepository.findAllBySenderIdOrReceiverId(userId, userId);
    }
}
