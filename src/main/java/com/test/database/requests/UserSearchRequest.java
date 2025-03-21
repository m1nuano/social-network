package com.test.database.requests;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserSearchRequest {

    @Size(max = 100, message = "Maximum 100 characters")
    private String username;

    @Size(max = 100, message = "Maximum 100 characters")
    private String email;

    @Size(max = 100, message = "Maximum 100 characters")
    private String firstName;

    @Size(max = 100, message = "Maximum 100 characters")
    private String lastName;
}
