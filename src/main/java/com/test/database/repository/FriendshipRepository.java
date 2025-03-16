package com.test.database.repository;

import com.test.database.model.Friendship;
import com.test.database.model.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findAllBySenderIdOrReceiverId(Long senderId, Long receiverId);

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Friendship> findAllBySenderIdOrReceiverIdAndStatus(Long senderId, Long receiverId, FriendshipStatus status);

    Optional<Friendship> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Friendship> findAllByReceiverIdAndStatus(Long receiverId, FriendshipStatus status);
}
