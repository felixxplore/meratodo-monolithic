package com.felix.meratodo.service;

import com.felix.meratodo.dto.UserRequestDto;
import com.felix.meratodo.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
}