package com.test.service;

import com.test.database.dto.MessageDto;
import com.test.database.mapper.MessageMapper;
import com.test.database.model.Message;
import com.test.database.repository.MessageRepository;
import com.test.database.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final RelationshipService relationshipService;

    public MessageDto toDTO(Message message) {
        return messageMapper.toDto(message);
    }

    public Message toEntity(MessageDto messageDto) {
        return messageMapper.toEntity(messageDto);
    }

    @Transactional
    public Message addMessage(Message message) {
        log.info("Adding new message from user ID: {} to user ID: {}", message.getSender().getId(), message.getReceiver().getId());
        Message savedMessage = messageRepository.save(message);
        log.info("Message successfully added with ID: {}", savedMessage.getId());
        return savedMessage;
    }

    @Transactional(readOnly = true)
    public Message getMessageById(Long messageId) {
        log.info("Fetching message with ID: {}", messageId);
        return messageRepository.findById(messageId)
                                .orElseThrow(() -> {
                                    log.error("Message not found with ID: {}", messageId);
                                    return new RuntimeException("Message not found with ID: " + messageId);
                                });
    }

    @Transactional(readOnly = true)
    public List<Message> getAllMessages() {
        log.info("Fetching all messages");
        List<Message> messages = messageRepository.findAll();
        log.info("Fetched {} messages", messages.size());
        return messages;
    }

    @Transactional
    public Message updateMessage(Message message) {
        log.info("Updating message with ID: {}", message.getId());
        if (!messageRepository.existsById(message.getId())) {
            log.error("Message not found with ID: {}", message.getId());
            throw new RuntimeException("Message not found with ID: " + message.getId());
        }
        Message updatedMessage = messageRepository.save(message);
        log.info("Message with ID: {} successfully updated", message.getId());
        return updatedMessage;
    }

    @Transactional
    public boolean deleteMessage(Long messageId) {
        log.info("Deleting message with ID: {}", messageId);
        if (!messageRepository.existsById(messageId)) {
            log.error("Message not found with ID: {}", messageId);
            throw new RuntimeException("Message not found with ID: " + messageId);
        }
        messageRepository.deleteById(messageId);
        log.info("Message with ID: {} successfully deleted", messageId);
        return true;
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessages(int page, int size) {
        User currentUser = userService.getCurrentUser();
        Page<Message> messagePage = messageRepository.findByReceiverIdOrderBySentAtDesc(currentUser.getId(), PageRequest.of(page, size));
        log.info("Fetching messages for user with ID: {} - page: {} size: {}", currentUser.getId(), page, size);
        return messagePage.stream()
                          .map(messageMapper::toDto)
                          .collect(Collectors.toList());
    }

    @Transactional
    public MessageDto sendMessage(Long receiverId, String messageText) {
        User sender = userService.getCurrentUser();
        User receiver = userService.getUserById(receiverId);

        if (relationshipService.isBlocked(receiver.getId(), sender.getId())) {
            log.warn("Access denied: User ID: {} is blocked by user ID: {}", sender.getId(), receiver.getId());
            throw new AccessDeniedException("Access denied: receiver has blocked you.");
        }

        Message message = Message.builder()
                                 .sender(sender)
                                 .receiver(receiver)
                                 .message(messageText)
                                 .sentAt(LocalDateTime.now())
                                 .build();

        Message savedMessage = messageRepository.save(message);
        log.info("Message sent from user {} to user {} with message ID {}",
                sender.getId(), receiver.getId(), savedMessage.getId());
        return messageMapper.toDto(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getConversation(Long userId1, Long userId2) {
        log.info("Fetching conversation between user ID: {} and user ID: {}", userId1, userId2);
        List<Message> messages = messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                userId1, userId2, userId2, userId1
        );
        log.info("Fetched {} messages between user ID: {} and user ID: {}", messages.size(), userId1, userId2);
        return messages.stream()
                       .map(messageMapper::toDto)
                       .toList();
    }
}
