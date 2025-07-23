package com.felix.meratodo.service;

import com.felix.meratodo.dto.UserResponseDto;
import com.felix.meratodo.dto.UserUpdateDTO;
import com.felix.meratodo.exception.ResourceNotFoundException;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllStudents() {
        return userRepository.findAll();
    }
    public User updateProfile(Long id, UserUpdateDTO dto ) {
        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found "));
        user.setName(dto.getName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setRole(dto.getRole());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if(!userRepository.existsById(id)){
            throw new UsernameNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));

         return UserResponseDto.builder()
                 .id(user.getId())
                 .name(user.getName())
                 .email(user.getEmail())
                 .avatarUrl(user.getAvatarUrl())
                 .role(user.getRole())
                 .createdAt(user.getCreatedAt())
                 .updatedAt(LocalDateTime.now())
                 .build();

    }
}
