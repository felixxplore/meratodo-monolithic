package com.felix.meratodo.service;


import com.felix.meratodo.dto.UserLoginDTO;
import com.felix.meratodo.dto.UserRegistrationDTO;
import com.felix.meratodo.model.PasswordResetToken;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.TokenRepository;
import com.felix.meratodo.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(12);

    // methods
     public User register(UserRegistrationDTO dto) {

        if(userRepository.existsByEmail(dto.getEmail())){
            throw  new IllegalArgumentException("Email already exists");
        }
        User user=new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);

    }

     public Map<String, String> login(UserLoginDTO dto) {

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        if(authenticate.isAuthenticated()){
            String token=jwtService.generateToken(dto.getEmail());
            Map<String, String> response=new HashMap<>();
            response.put("token",token);
            return response;
        }
        return null;
    }

     public void requestPasswordReset(String email) throws MessagingException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken=new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(passwordResetToken);

         System.out.println("request password reset....");

        emailService.sendPasswordResetEmail(email, token);
    }

     public void resetPassword(String token, String newPassword) {

         PasswordResetToken resetToken = tokenRepository.findByToken(token).orElseThrow(()-> new RuntimeException("Invalid Token"));

         if(resetToken.isExpired()){
             throw new RuntimeException("Token expired");
         }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}
