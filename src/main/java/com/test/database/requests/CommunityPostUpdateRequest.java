package com.test.database.requests;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityPostUpdateRequest {

    @Size(max = 100, message = "Maximum 100 characters")
    private String postContent;
}