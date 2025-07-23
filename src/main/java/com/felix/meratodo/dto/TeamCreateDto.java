package com.felix.meratodo.dto;

import com.felix.meratodo.model.Project;
import com.felix.meratodo.model.TeamMembership;
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
    private Set<Project> projects;
    private Set<TeamMembership> memberships;
}
