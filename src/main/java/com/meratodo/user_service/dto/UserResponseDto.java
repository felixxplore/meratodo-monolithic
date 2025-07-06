package com.meratodo.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
