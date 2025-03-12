package com.test.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
