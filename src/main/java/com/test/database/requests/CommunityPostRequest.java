package com.test.database.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityPostRequest {
    @NotNull(message = "Community ID must not be null")
    private Long communityId;

    @NotBlank(message = "Post content must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String postContent;
}

