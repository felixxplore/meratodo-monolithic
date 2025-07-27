package com.felix.meratodo.dto;

import com.felix.meratodo.model.Project;
import com.felix.meratodo.model.TeamMembership;
import com.felix.meratodo.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class TeamCreateDto {

    @NotBlank(message = "Team name must be required")
    private String name;
    private String description;
    private User owner;
    private Set<Project> projects;
    private Set<TeamMembership> memberships;
}
