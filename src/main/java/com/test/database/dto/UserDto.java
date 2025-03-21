package com.test.database.dto;

import com.test.database.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "The user name cannot be empty")
    @Size(min = 3, max = 100, message = "Username should contain from 3 to 100 characters")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Incorrect format email")
    @Size(max = 100, message = "Maximum 100 characters")
    private String email;

    @NotBlank(message = "The name cannot be empty")
    @Size(max = 100, message = "The name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "The lastname cannot be empty")
    @Size(max = 100, message = "The lastname cannot exceed 100 characters")
    private String lastName;

    @Size(max = 100, message = "The biography cannot exceed 100 characters")
    private String bio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "User role must not be null")
    private UserRole userRole;
}
