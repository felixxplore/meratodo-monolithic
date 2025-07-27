package com.felix.meratodo.dto;

import com.felix.meratodo.model.Team;
import com.felix.meratodo.model.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private boolean isArchived;
    private UserResponseDto owner;
    private TeamResponseDto team;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
