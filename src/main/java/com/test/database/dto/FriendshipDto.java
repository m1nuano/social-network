package com.test.database.dto;

import com.test.database.model.enums.FriendshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipDto {
    private Long id;

    @NotNull(message = "Sender ID must not be null")
    private Long senderId;

    @NotNull(message = "Receiver ID must not be null")
    private Long receiverId;

    @NotNull(message = "Friendship status must not be null")
    private FriendshipStatus status;

    private LocalDateTime createdAt;
}
