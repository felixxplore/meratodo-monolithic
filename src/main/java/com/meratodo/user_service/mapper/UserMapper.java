package com.meratodo.user_service.mapper;

import com.meratodo.user_service.dto.UserRequestDto;
import com.meratodo.user_service.dto.UserResponseDto;
import com.meratodo.user_service.model.User;
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
