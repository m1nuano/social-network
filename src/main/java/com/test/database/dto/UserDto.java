package com.test.database.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "The user name cannot be empty")
    @Size(min = 3, max = 50, message = "User name should contain from 3 to 50 characters")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect format email")
    private String email;

    @NotBlank(message = "The name cannot be empty")
    @Size(min = 2, max = 50, message = "The name should contain from 2 to 50 characters")
    private String firstName;

    @NotBlank(message = "The lastname cannot be empty")
    @Size(min = 2, max = 50, message = "The lastname should contain from 2 to 50 characters")
    private String lastName;

    @Size(max = 500, message = "The biography cannot exceed 500 characters")
    private String bio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userRole;
}
