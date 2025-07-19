package com.felix.meratodo.dto;

import com.felix.meratodo.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotNull
    private String name;

    private String avatarUrl;

    private UserRole role;

}
