package com.felix.meratodo.dto;

import com.felix.meratodo.enums.Role;
import com.felix.meratodo.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;


public record UserResponseDto(Long id, String name, String email, String avatarUrl, Role role, LocalDateTime createdAt,
                              LocalDateTime updatedAt) {

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}
