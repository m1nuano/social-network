package com.test.database.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditProfileRequest {
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Must be in email format, like this: test@example.com")
    @Size(max = 100, message = "Maximum 100 characters")
    private String email;

    @Size(max = 100, message = "Maximum 100 characters")
    private String firstName;

    @Size(max = 100, message = "Maximum 100 characters")
    private String lastName;

    @Size(max = 100, message = "Maximum 100 characters")
    private String bio;
}
