package com.test.database.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequest {
    @NotBlank(message = "Post content must not be empty")
    private String postContent;
}
