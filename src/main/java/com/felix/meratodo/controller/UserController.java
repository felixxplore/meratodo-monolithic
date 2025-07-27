package com.felix.meratodo.controller;


import com.felix.meratodo.dto.UserResponseDto;
import com.felix.meratodo.dto.UserUpdateDTO;
import com.felix.meratodo.model.User;
import com.felix.meratodo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // get all Users
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllStudents());
    }

    // delete user by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id){
        userService.deleteUserById(id);
        return  ResponseEntity.ok("User Deleted Successfully");
    }

    // update profile
    @PutMapping("/{id}")
    public  ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto){
        UserResponseDto userUpdated= userService.updateProfile(id, dto);
        return ResponseEntity.ok(userUpdated);
    }

    // get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
