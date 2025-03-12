package com.test.dto;

import com.test.model.enums.LikeObjectType;
import com.test.model.enums.LikeType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {
    private Long id;
    private Long userId;
    private Long objectId;
    private LikeObjectType objectType;
    private LikeType likeType;
    private LocalDateTime createdAt;
}
