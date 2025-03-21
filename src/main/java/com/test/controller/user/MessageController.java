package com.test.controller.user;

import com.test.database.dto.MessageDto;
import com.test.database.requests.MessageSendRequest;
import com.test.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody MessageSendRequest request) {
        MessageDto messageDto = messageService.sendMessage(request.getReceiverId(), request.getMessageContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MessageDto> inboxMessages = messageService.getMessages(page, size);
        return ResponseEntity.ok(inboxMessages);
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDto>> getConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        List<MessageDto> conversation = messageService.getConversation(userId1, userId2);
        return ResponseEntity.ok(conversation);
    }
}
