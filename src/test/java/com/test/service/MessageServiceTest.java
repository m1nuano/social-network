package com.test.service;

import com.test.database.dto.MessageDto;
import com.test.database.mapper.MessageMapper;
import com.test.database.model.Message;
import com.test.database.model.User;
import com.test.database.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private UserService userService;

    @Mock
    private RelationshipService relationshipService;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;
    private Message message;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        receiver = new User();
        receiver.setId(2L);

        message = new Message();
        message.setId(100L);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessage("Hello");
        message.setSentAt(LocalDateTime.now());

        messageDto = new MessageDto();
        messageDto.setId(100L);
        messageDto.setSenderId(sender.getId());
        messageDto.setReceiverId(receiver.getId());
        messageDto.setMessage("Hello");
    }

    @Test
    void testSendMessage_Success() {
        when(userService.getCurrentUser()).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        when(relationshipService.isBlocked(receiver.getId(), sender.getId())).thenReturn(false);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        MessageDto result = messageService.sendMessage(receiver.getId(), "Hello");

        assertNotNull(result);
        assertEquals(messageDto.getId(), result.getId());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void testSendMessage_Fail_BlockedUser() {
        when(userService.getCurrentUser()).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        when(relationshipService.isBlocked(receiver.getId(), sender.getId())).thenReturn(true);

        Exception exception = assertThrows(AccessDeniedException.class, () ->
                messageService.sendMessage(receiver.getId(), "Blocked message"));

        assertEquals("Access denied: receiver has blocked you.", exception.getMessage());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void testGetMessageById_Success() {
        when(messageRepository.findById(100L)).thenReturn(Optional.of(message));

        Message result = messageService.getMessageById(100L);

        assertNotNull(result);
        assertEquals(message.getId(), result.getId());
    }

    @Test
    void testGetMessageById_NotFound() {
        when(messageRepository.findById(100L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                messageService.getMessageById(100L));

        assertEquals("Message not found with ID: 100", exception.getMessage());
    }

    @Test
    void testDeleteMessage_Success() {
        when(messageRepository.existsById(100L)).thenReturn(true);

        boolean result = messageService.deleteMessage(100L);

        assertTrue(result);
        verify(messageRepository).deleteById(100L);
    }

    @Test
    void testDeleteMessage_NotFound() {
        when(messageRepository.existsById(100L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                messageService.deleteMessage(100L));

        assertEquals("Message not found with ID: 100", exception.getMessage());
        verify(messageRepository, never()).deleteById(100L);
    }

    @Test
    void testGetConversation() {
        List<Message> messages = List.of(message);
        when(messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                sender.getId(), receiver.getId(), receiver.getId(), sender.getId()))
                .thenReturn(messages);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        List<MessageDto> result = messageService.getConversation(sender.getId(), receiver.getId());

        assertEquals(1, result.size());
        assertEquals(messageDto.getMessage(), result.get(0).getMessage());
    }
}
