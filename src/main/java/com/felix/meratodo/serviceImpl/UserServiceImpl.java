package com.felix.meratodo.serviceImpl;

import com.felix.meratodo.dto.UserRequestDto;
import com.felix.meratodo.dto.UserResponseDto;
import com.felix.meratodo.exception.UserAlreadyExistsException;
import com.felix.meratodo.mapper.UserMapper;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.UserRepository;
import com.felix.meratodo.service.UserService;
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
