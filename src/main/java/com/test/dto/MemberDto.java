package com.test.dto;

import com.test.model.enums.MemberRole;
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
