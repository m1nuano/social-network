package com.test.dto;

import com.test.model.enums.FriendshipStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private FriendshipStatus status;
    private LocalDateTime createdAt;

}
