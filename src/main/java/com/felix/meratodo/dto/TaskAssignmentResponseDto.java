package com.felix.meratodo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskAssignmentResponseDto {

    private Long id;
    private UserResponseDto user;
    private TaskResponseDto task;
    private LocalDateTime createdAt;
}
