package com.test.service;

import com.test.database.model.Friendship;
import com.test.database.model.enums.FriendshipStatus;
import com.test.database.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipService {

    private final FriendshipRepository friendshipRepository;

    @Transactional(readOnly = true)
    public Friendship getFriendshipBetween(Long userId1, Long userId2) {
        log.info("Fetching friendship between user ID: {} and user ID: {}", userId1, userId2);
        return friendshipRepository.findBySenderIdAndReceiverId(userId1, userId2)
                                   .orElseGet(() -> {
                                       log.info("Friendship not found with sender ID: {} and receiver ID: {}, trying the reverse", userId2, userId1);
                                       return friendshipRepository.findBySenderIdAndReceiverId(userId2, userId1).orElse(null);
                                   });
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(Long blockerId, Long blockedUserId) {
        log.info("Checking if user ID: {} has blocked user ID: {}", blockerId, blockedUserId);
        Friendship friendship = getFriendshipBetween(blockerId, blockedUserId);
        boolean isBlocked = friendship != null
                && friendship.getStatus() == FriendshipStatus.BLOCKED
                && friendship.getSender().getId().equals(blockerId);
        if (isBlocked) {
            log.info("User ID: {} has blocked user ID: {}", blockerId, blockedUserId);
        } else {
            log.info("User ID: {} has not blocked user ID: {}", blockerId, blockedUserId);
        }
        return isBlocked;
    }
}
