package com.felix.meratodo.dto;

import com.felix.meratodo.enums.TaskPriority;
import com.felix.meratodo.enums.TaskStatus;
import com.felix.meratodo.model.Project;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskPriority priority;
    private TaskStatus status;
    private List<Long> assigneeIds;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
