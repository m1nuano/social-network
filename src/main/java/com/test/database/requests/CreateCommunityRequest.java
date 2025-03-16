package com.test.database.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommunityRequest {

    @NotBlank(message = "Community name must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String name;

    @NotBlank(message = "Community description must not be empty")
    @Size(max = 200, message = "Maximum 200 characters")
    private String description;
}
