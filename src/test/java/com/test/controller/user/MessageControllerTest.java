package com.test.controller.user;

import com.test.database.dto.MessageDto;
import com.test.database.requests.MessageSendRequest;
import com.test.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        messageDto = MessageDto.builder()
                               .id(1L)
                               .receiverId(2L)
                               .message("Test message")
                               .build();
    }

    @Test
    void testSendMessage() {
        MessageSendRequest request = new MessageSendRequest();
        request.setReceiverId(2L);
        request.setMessageContent("Test message");

        Mockito.when(messageService.sendMessage(
                       ArgumentMatchers.eq(request.getReceiverId()),
                       ArgumentMatchers.eq(request.getMessageContent())))
               .thenReturn(messageDto);

        ResponseEntity<MessageDto> response = messageController.sendMessage(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(messageDto.getId(), response.getBody().getId());
        assertEquals(messageDto.getMessage(), response.getBody().getMessage());
    }

    @Test
    void testGetMessages() {
        int page = 0;
        int size = 20;
        List<MessageDto> messages = Arrays.asList(messageDto);

        Mockito.when(messageService.getMessages(page, size))
               .thenReturn(messages);

        ResponseEntity<List<MessageDto>> response = messageController.getMessages(page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(messageDto.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testGetConversation() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        List<MessageDto> conversation = Arrays.asList(messageDto);

        Mockito.when(messageService.getConversation(userId1, userId2))
               .thenReturn(conversation);

        ResponseEntity<List<MessageDto>> response = messageController.getConversation(userId1, userId2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(messageDto.getMessage(), response.getBody().get(0).getMessage());
    }
}
