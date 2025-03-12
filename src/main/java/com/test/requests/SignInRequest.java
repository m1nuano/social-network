package com.test.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {
    @Size(min = 5, max = 50)
    @NotBlank
    private String username;

    @Size(min = 4, max = 255)
    @NotBlank
    private String password;
}
