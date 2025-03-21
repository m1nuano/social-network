package com.test.database.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityListDto {
    private Long id;

    @NotBlank(message = "Community name must not be empty")
    @Size(max = 100, message = "Maximum 100 characters")
    private String name;

    @NotBlank(message = "Community description must not be empty")
    @Size(max = 200, message = "Maximum 200 characters")
    private String description;

    @NotNull(message = "Participants count must not be null")
    private Long participantsCount;
}
