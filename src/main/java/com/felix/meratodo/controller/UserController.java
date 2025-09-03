package com.felix.meratodo.controller;


import com.felix.meratodo.dto.UserResponseDto;
import com.felix.meratodo.dto.UserUpdateDTO;
import com.felix.meratodo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name="User APIs", description = "User Management")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    // get all Users
    @GetMapping
    @ApiResponse(
            responseCode = "200",
            description = "successfully get all users"
    )
    @Operation(summary = "get all users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable){
        log.info("Fetching users with pageable: {}", pageable);
         Page<UserResponseDto> allUsers = userService.getAllUsers(pageable);
        return ResponseEntity.ok(allUsers);
    }

    // delete user by id
    @DeleteMapping("/{id}")
    @Operation(summary = "delete user by id")
    public ResponseEntity<String> deleteUserById( @Parameter(description = "ID of user required to delete", required = true) @PathVariable Long id){
        userService.deleteUserById(id);
        return  ResponseEntity.ok("User Deleted Successfully");
    }

    // update profile
    @PutMapping("/{id}")
    @Operation(summary = "update user by id")
    public  ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto){
        UserResponseDto userUpdated= userService.updateProfile(id, dto);
        return ResponseEntity.ok(userUpdated);
    }

    // get user by id
    @GetMapping("/{id}")
    @Operation(summary = "get user by id")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
