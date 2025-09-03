package com.felix.meratodo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotNull
    private String name;

    private String avatarUrl;



}
