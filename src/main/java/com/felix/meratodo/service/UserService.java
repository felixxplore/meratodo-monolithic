package com.felix.meratodo.service;

import com.felix.meratodo.dto.UserResponseDto;
import com.felix.meratodo.dto.UserUpdateDTO;
import com.felix.meratodo.exception.ResourceNotFoundException;
import com.felix.meratodo.mapper.UserMapper;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private static UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserResponseDto> getAllStudents() {
        return userMapper.toDto(userRepository.findAll());
    }
    public UserResponseDto updateProfile(Long id, UserUpdateDTO dto ) {
        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found "));
        user.setName(dto.getName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setRole(dto.getRole());
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser=userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    public void deleteUserById(Long id) {
        if(!userRepository.existsById(id)){
            throw new UsernameNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toDto(user);

    }
    public static User getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found."));

    }
}
