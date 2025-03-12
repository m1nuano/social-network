package com.test.service;

import com.test.dto.MessageDto;
import com.test.mapper.MessageMapper;
import com.test.model.Message;
import com.test.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

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
}
