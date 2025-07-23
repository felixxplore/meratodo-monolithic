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
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getAllStudents());
    }

    // delete user by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@RequestParam Long id){
        userService.deleteUserById(id);
        return  ResponseEntity.ok("User Deleted Successfully");
    }

    // update profile
    @PutMapping("/{id}")
    public  ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto){
        User user = userService.updateProfile(id, dto);
        return ResponseEntity.ok(user);
    }

    // get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@RequestParam Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserById(id));
    }
}
