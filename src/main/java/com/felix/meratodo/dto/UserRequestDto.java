package com.felix.meratodo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDto {

    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 6, max = 20, message = "Password must be between 6 to 20 characters.")
    private String password;


}
