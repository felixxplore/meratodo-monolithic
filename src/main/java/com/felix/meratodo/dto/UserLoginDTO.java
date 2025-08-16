package com.felix.meratodo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotNull @Email
    @Schema(description = "email required for login", example = "xyz07@gmail.com")
    private String email;

    @NotNull
    @Schema(description = "password required for login", example = "xyz123")
    private String password;

    private String otp;
}
