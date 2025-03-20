package com.test.database.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "Username must not be empty")
    @Size(min = 3, max = 100, message = "Username should contain from 3 to 100 characters")
    private String username;

    @NotBlank(message = "Email must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Must be in email format, like this: test@example.com")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String password;

    @NotBlank(message = "Firstname must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String firstName;

    @NotBlank(message = "Lastname must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String lastName;

    @Size(max = 100, message = "Maximum 100 characters")
    private String bio;
}
