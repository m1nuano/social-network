package com.test.database.dto;

import com.test.database.model.enums.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long id;

    @NotNull(message = "Community ID must not be null")
    private Long communityId;

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Member role must not be null")
    private MemberRole memberRole;

    private LocalDateTime joinedAt;
}
