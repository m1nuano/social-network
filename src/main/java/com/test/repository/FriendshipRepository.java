package com.test.repository;

import com.test.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Friendship> findAllBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
