package com.felix.meratodo.dto;

import com.felix.meratodo.enums.TaskPriority;
import com.felix.meratodo.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskRequestDto {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskPriority priority;

    @NotNull(message = "Project Id required")
    private Long projectId;

    private TaskStatus status;

    private List<Long> assigneesIds;


}
