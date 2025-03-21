package com.test.database.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {
    @NotBlank(message = "Post content must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String postContent;
}
