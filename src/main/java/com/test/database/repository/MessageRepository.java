package com.test.database.repository;

import com.test.database.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByReceiverIdOrderBySentAtDesc(Long receiverId, Pageable pageable);
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);
}
