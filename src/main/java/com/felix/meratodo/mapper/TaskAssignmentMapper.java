package com.felix.meratodo.mapper;

import com.felix.meratodo.dto.TaskAssignmentResponseDto;
import com.felix.meratodo.model.TaskAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class TaskAssignmentMapper {

    private static final TaskAssignmentMapper INSTANCE= Mappers.getMapper(TaskAssignmentMapper.class);

    public abstract TaskAssignmentResponseDto toDto(TaskAssignment taskAssignment);
}
