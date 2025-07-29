package com.felix.meratodo.mapper;

import com.felix.meratodo.dto.TaskResponseDto;
import com.felix.meratodo.model.Task;
import com.felix.meratodo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  TaskMapper INSTANCE= Mappers.getMapper(TaskMapper.class);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignees", target = "assigneeIds",  qualifiedByName = "mapAssigneesToIds")
     TaskResponseDto toDto(Task task);

    List<TaskResponseDto> toDtoList(List<Task> tasks);

    @Named("mapAssigneesToIds")
    default List<Long> mapAssigneesToIds(Set<User> assignees){
        if(assignees==null) return null;
        return assignees.stream().map(User::getId).collect(Collectors.toList());
    }


}
