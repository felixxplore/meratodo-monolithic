package com.meratodo.user_service.controller;

import com.meratodo.user_service.dto.UserRequestDto;
import com.meratodo.user_service.dto.UserResponseDto;
import com.meratodo.user_service.serviceImpl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto){
        UserResponseDto user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getAllUsers());
    }
}
