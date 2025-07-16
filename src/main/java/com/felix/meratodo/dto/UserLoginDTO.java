package com.felix.meratodo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotNull @Email
    private String email;

    @NotNull
    private String password;
}
