package com.felix.meratodo.dto;

import lombok.Data;

@Data
public class TeamResponseDto {

    private Long id;
    private String name;
    private String description;
    private UserResponseDto owner;

}
