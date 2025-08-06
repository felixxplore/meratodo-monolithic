package com.felix.meratodo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message="Email is required") @Email
    private String email;

    @NotBlank(message = "Password can not be blank or minimum length 6.") @Size(min = 6)
    private String password;


}
