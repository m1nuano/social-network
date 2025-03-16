package com.test.database.dto;

import com.test.database.model.enums.FriendshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    @NotNull(message = "User must not be null")
    private UserDto user;

    private FriendshipStatus friendshipStatus;
    private List<CommunityDto> communities;
}
