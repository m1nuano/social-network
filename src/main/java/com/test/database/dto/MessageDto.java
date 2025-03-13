package com.test.database.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime sentAt;
}
