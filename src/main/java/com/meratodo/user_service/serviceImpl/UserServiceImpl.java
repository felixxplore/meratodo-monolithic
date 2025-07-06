package com.meratodo.user_service.serviceImpl;

import com.meratodo.user_service.dto.UserRequestDto;
import com.meratodo.user_service.dto.UserResponseDto;
import com.meratodo.user_service.exception.UserAlreadyExistsException;
import com.meratodo.user_service.mapper.UserMapper;
import com.meratodo.user_service.model.User;
import com.meratodo.user_service.repository.UserRepository;
import com.meratodo.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserResponseDto createUser(UserRequestDto userRequestDto){

       userRepository.findByEmail(userRequestDto.getEmail()).ifPresent(user->{
           throw new UserAlreadyExistsException(userRequestDto.getEmail());
       });

        User user = userMapper.toUser(userRequestDto);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public List<UserResponseDto> getAllUsers(){
        List<UserResponseDto> userResponseDtoList=new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userResponseDtoList.add(userMapper.toDto(user));
        }

        return userResponseDtoList;
    }
}
