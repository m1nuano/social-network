package com.test.database.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;

    @NotNull(message = "Sender ID must not be null")
    private Long senderId;

    @NotNull(message = "Receiver ID must not be null")
    private Long receiverId;

    @NotBlank(message = "Message content must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String message;

    private LocalDateTime sentAt;
}
