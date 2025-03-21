package com.test.controller.admin;

import com.test.database.dto.MessageDto;
import com.test.database.model.Message;
import com.test.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private AdminMessageController adminMessageController;

    private Message message;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        message = Message.builder()
                         .id(1L)
                         .message("Test Message")
                         .sentAt(LocalDateTime.now())
                         .build();

        messageDto = MessageDto.builder()
                               .id(1L)
                               .message("Test Message")
                               .sentAt(LocalDateTime.now())
                               .build();
    }

    @Test
    void testListMessages() {
        when(messageService.getAllMessages()).thenReturn(List.of(message));
        when(messageService.toDTO(any(Message.class))).thenReturn(messageDto);

        ResponseEntity<List<MessageDto>> response = adminMessageController.listMessages();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Message", response.getBody().get(0).getMessage());
    }

    @Test
    void testGetMessage() {
        when(messageService.getMessageById(1L)).thenReturn(message);
        when(messageService.toDTO(message)).thenReturn(messageDto);

        ResponseEntity<MessageDto> response = adminMessageController.getMessage(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Message", response.getBody().getMessage());
    }

    @Test
    void testCreateMessage() {
        when(messageService.toEntity(any(MessageDto.class))).thenReturn(message);
        when(messageService.addMessage(any(Message.class))).thenReturn(message);
        when(messageService.toDTO(any(Message.class))).thenReturn(messageDto);

        ResponseEntity<MessageDto> response = adminMessageController.createMessage(messageDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Message", response.getBody().getMessage());
    }

    @Test
    void testUpdateMessage() {
        when(messageService.toEntity(any(MessageDto.class))).thenReturn(message);
        when(messageService.updateMessage(any(Message.class))).thenReturn(message);
        when(messageService.toDTO(any(Message.class))).thenReturn(messageDto);

        ResponseEntity<MessageDto> response = adminMessageController.updateMessage(1L, messageDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Message", response.getBody().getMessage());
    }

    @Test
    void testDeleteMessage_Success() {
        when(messageService.deleteMessage(1L)).thenReturn(true);

        ResponseEntity<Void> response = adminMessageController.deleteMessage(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(messageService, times(1)).deleteMessage(eq(1L));
    }

    @Test
    void testDeleteMessage_NotFound() {
        when(messageService.deleteMessage(1L)).thenReturn(false);

        ResponseEntity<Void> response = adminMessageController.deleteMessage(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
