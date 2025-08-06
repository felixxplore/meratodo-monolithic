package com.felix.meratodo.mapper;

import com.felix.meratodo.dto.TaskRequestDto;
import com.felix.meratodo.dto.TaskResponseDto;
import com.felix.meratodo.model.Task;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public abstract class TaskMapper {

    public static final TaskMapper INSTANCE= Mappers.getMapper(TaskMapper.class);

    @Autowired
    private UserRepository userRepository;


    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignees", target = "assigneeIds",  qualifiedByName = "mapAssigneesToIds")
    public abstract TaskResponseDto toDto(Task task);

    public abstract List<TaskResponseDto> toDtoList(List<Task> tasks);

    @Named("mapAssigneesToIds")
    protected List<Long> mapAssigneesToIds(Set<User> assignees){
        if(assignees==null) return null;
        return assignees.stream().map(User::getId).collect(Collectors.toList());
    }

    @Mapping(source = "projectId",target = "project.id" )
    @Mapping(source = "assigneesIds",target = "assignees", qualifiedByName = "mapIdsToAssignees")
    public abstract  Task toEntity(TaskRequestDto dto);

    @Named("mapIdsToAssignees")
    protected Set<User> mapIdsToAssignees(List<Long> ids){

        return new HashSet<>(userRepository.findAllById(ids));
    }


}
