package com.felix.meratodo.controller;

import com.felix.meratodo.dto.UserLoginDTO;
import com.felix.meratodo.dto.UserRegistrationDTO;
import com.felix.meratodo.dto.UserUpdateDTO;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.UserRepository;
import com.felix.meratodo.serviceImpl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;


    @RestController
    @RequestMapping("/api/users")
    public class UserController {

        @Autowired
        private UserServiceImpl userService;

        @Autowired
        private UserRepository userRepository;

        @GetMapping
        public ResponseEntity<List<User>> getAllStudents(){
            return ResponseEntity.status(HttpStatus.FOUND).body(userService.getAllStudents());
        }

        @GetMapping("/csrf-token")
        public CsrfToken getCsrfToken(HttpServletRequest request){
            return (CsrfToken) request.getAttribute("_csrf");
        }


        @PostMapping("/register")
        public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDTO dto){
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(dto));
        }

        @PostMapping("/login")
        public String login(@Valid @RequestBody UserLoginDTO dto){
            return userService.login(dto);

        }

        @PutMapping("/{id}")
        public  ResponseEntity<User> updateProfile(@PathVariable Long id,@Valid @RequestBody UserUpdateDTO dto){
            User user = userService.updateProfile(id, dto);

            return ResponseEntity.ok(user);
        }

        @PutMapping("/{id}/role")
        public ResponseEntity<User> updateRole(@PathVariable Long id, String role, Authentication auth){

            User currentUser = userRepository.findByEmail(auth.getDeclaringClass().getName()).get();
            return ResponseEntity.ok(userService.updateRole(id,role,currentUser));

        }


    }
