package com.felix.meratodo.dto;

import com.felix.meratodo.enums.TeamRole;
import com.felix.meratodo.model.Team;
import com.felix.meratodo.model.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamMembershipResponseDto {
    private Long id;
    private UserResponseDto user;
    private Team team;
    private TeamRole teamRole;
    private LocalDateTime createdAt;

}
