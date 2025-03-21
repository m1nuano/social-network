package com.test.database.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {
    @Size(min = 3, max = 100, message = "Username should contain from 3 to 100 characters")
    @NotBlank
    private String username;

    @Size(max = 100, message = "Maximum 100 characters")
    @NotBlank
    private String password;
}
