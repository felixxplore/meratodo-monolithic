package com.felix.meratodo.mapper;

import com.felix.meratodo.dto.TeamMembershipResponseDto;
import com.felix.meratodo.model.TeamMembership;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMembershipMapper {

    TeamMembershipMapper INSTANCE= Mappers.getMapper(TeamMembershipMapper.class);

    List<TeamMembershipResponseDto> toDto(List<TeamMembership> teamMemberships);

}
