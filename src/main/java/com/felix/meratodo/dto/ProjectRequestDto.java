package com.felix.meratodo.dto;

import com.felix.meratodo.enums.ProjectPermission;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectRequestDto {

    private String name;
    private String description;
    private LocalDateTime deadline;
    private boolean isPublic;
}
