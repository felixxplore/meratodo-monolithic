package com.felix.meratodo.mapper;

import com.felix.meratodo.dto.UserResponseDto;
import com.felix.meratodo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE= Mappers.getMapper(UserMapper.class);

    UserResponseDto toDto(User user);
    List<UserResponseDto> toDto(List<User> users);
}
