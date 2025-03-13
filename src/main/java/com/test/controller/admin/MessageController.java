package com.test.controller.admin;

import com.test.database.dto.MessageDto;
import com.test.service.MessageService;
import com.test.database.model.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDto>> listMessages() {
        List<MessageDto> messages = messageService.getAllMessages()
                .stream()
                .map(messageService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessage(@PathVariable("id") Long id) {
        Message message = messageService.getMessageById(id);
        return ResponseEntity.ok(messageService.toDTO(message));
    }

    @PostMapping
    public ResponseEntity<MessageDto> createMessage(@Valid @RequestBody MessageDto messageDto) {
        Message message = messageService.toEntity(messageDto);
        Message createdMessage = messageService.addMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.toDTO(createdMessage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(@PathVariable("id") Long id, @Valid @RequestBody MessageDto messageDto) {
        Message message = messageService.toEntity(messageDto);
        message.setId(id);
        Message updatedMessage = messageService.updateMessage(message);
        return ResponseEntity.ok(messageService.toDTO(updatedMessage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long id) {
        boolean isDeleted = messageService.deleteMessage(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
