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
public class CommunityPostDto {
    private Long id;

    @NotNull(message = "Community ID must not be null")
    private Long communityId;

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotBlank(message = "Post content must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String postContent;

    private LocalDateTime createdAt;
}
