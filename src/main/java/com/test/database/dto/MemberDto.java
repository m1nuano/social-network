package com.test.database.dto;

import com.test.database.model.enums.MemberRole;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long communityId;
    private Long userId;
    private MemberRole memberRole;
    private LocalDateTime joinedAt;
}
