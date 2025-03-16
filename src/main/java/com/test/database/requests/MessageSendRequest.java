package com.test.database.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageSendRequest {
    @NotNull(message = "ReceiverId cannot be null")
    private Long receiverId;

    @NotBlank(message = "Message content cannot be blank")
    @Size(max = 100, message = "Maximum 100 characters")
    private String messageContent;
}

