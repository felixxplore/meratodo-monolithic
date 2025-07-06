package com.meratodo.user_service.service;

import com.meratodo.user_service.dto.UserRequestDto;
import com.meratodo.user_service.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
}