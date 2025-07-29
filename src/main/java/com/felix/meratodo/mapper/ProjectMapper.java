package com.felix.meratodo.mapper;


import com.felix.meratodo.dto.ProjectRequestDto;
import com.felix.meratodo.dto.ProjectResponseDto;
import com.felix.meratodo.dto.TaskResponseDto;
import com.felix.meratodo.model.Project;
import com.felix.meratodo.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectMapper INSTANCE= Mappers.getMapper(ProjectMapper.class);

    List<ProjectResponseDto> toDto(List<Project> projects);
    ProjectResponseDto toDto(Project project);
    Project toEntity(ProjectRequestDto dto);
    Project toEntity(ProjectResponseDto dto);


}
