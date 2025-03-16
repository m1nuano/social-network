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
public class CommentDto {
    private Long id;

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Post ID must not be null")
    private Long postId;

    @NotBlank(message = "Comment content must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String content;

    private LocalDateTime createdAt;
}
