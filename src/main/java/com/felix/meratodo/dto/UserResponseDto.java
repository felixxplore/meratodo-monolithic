package com.felix.meratodo.dto;

import com.felix.meratodo.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String avatarUrl;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
