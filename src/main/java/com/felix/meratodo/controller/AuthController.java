package com.felix.meratodo.controller;

import com.felix.meratodo.dto.*;
import com.felix.meratodo.model.User;
import com.felix.meratodo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping("/api/auth")
    @Tag(name = "Auth APIs",  description = "Authentication Management")
    public class AuthController {

        @Autowired
        private AuthService authService;

        // register user
        @PostMapping("/register")
        @Operation(summary = "register user or user signup")
        public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO dto){
            return ResponseEntity.ok(authService.register(dto));
        }

        @PostMapping("/verify-email")
        @Operation(summary = "verify email")
        public ResponseEntity<?> verifyEmail(@RequestParam String token){
            authService.verifyEmail(token);
            return ResponseEntity.ok("Email Verified Successfully");
        }

        // login user
        @PostMapping("/login")
        @Operation(summary = "login or sign in")
        public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO dto){
            return ResponseEntity.ok(authService.login(dto));

        }

        // request password reset
        @PostMapping("/reset-password")
        @Operation(summary = "sent password reset link")
        public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest request ) throws MessagingException {
            authService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok("Password reset link sent");
        }

        @PostMapping("/reset-password/confirm")
        @Operation(summary = "update password")
        public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody PasswordResetRequest request){
            authService.resetPassword(token, request.getNewPassword());
            return ResponseEntity.ok("Password reset successful");
        }

    }
