package com.felix.meratodo.mapper;

import com.felix.meratodo.dto.UserRequestDto;
import com.felix.meratodo.dto.UserResponseDto;
import com.felix.meratodo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserRequestDto dto);

    UserResponseDto toDto(User user);
}
