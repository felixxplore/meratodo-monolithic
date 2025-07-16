package com.felix.meratodo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDTO {

    @NotNull
    private String name;

    @NotNull @Email
    private String email;

    @NotNull @Size(min = 6)
    private String password;

    private String avatarUrl;

}
