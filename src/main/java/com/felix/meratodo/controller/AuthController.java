package com.felix.meratodo.controller;

import com.felix.meratodo.dto.PasswordResetRequest;
import com.felix.meratodo.dto.UserLoginDTO;
import com.felix.meratodo.dto.UserRegistrationDTO;
import com.felix.meratodo.model.User;
import com.felix.meratodo.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
    @RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        private AuthService authService;

        // register user
        @PostMapping("/register")
        public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDTO dto){
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
        }

        // login user
        @PostMapping("/login")
        public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO dto){
            return ResponseEntity.ok(authService.login(dto));

        }

        // request password reset
        @PostMapping("/reset-password")
        public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest request ) throws MessagingException {
            authService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok("Password reset link sent");
        }

        @PostMapping("/reset-password/confirm")
        public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody PasswordResetRequest request){
            authService.resetPassword(token, request.getNewPassword());
            return ResponseEntity.ok("Password reset successful");
        }

    }
