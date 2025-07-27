package com.felix.meratodo.mapper;

import com.felix.meratodo.dto.TeamResponseDto;
import com.felix.meratodo.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    TeamMapper INSTANCE=Mappers.getMapper(TeamMapper.class);

    TeamResponseDto toDto(Team team);
    List<TeamResponseDto> toDto(List<Team> teams);

}
