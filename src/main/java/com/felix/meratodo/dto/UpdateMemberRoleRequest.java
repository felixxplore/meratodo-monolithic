package com.felix.meratodo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMemberRoleRequest {

    @NotNull(message = "UserId is required")
    private Long userId;

    @NotBlank(message = "Team role is required")
    private String teamRole;
}
