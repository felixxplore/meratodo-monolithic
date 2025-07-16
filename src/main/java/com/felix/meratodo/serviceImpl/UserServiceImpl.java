package com.felix.meratodo.serviceImpl;


import com.felix.meratodo.dto.UserLoginDTO;
import com.felix.meratodo.dto.UserRegistrationDTO;
import com.felix.meratodo.dto.UserUpdateDTO;
import com.felix.meratodo.enums.UserRole;
import com.felix.meratodo.exception.ResourceNotFoundException;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.UserRepository;
import com.felix.meratodo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(12);

    @Override
    public User register(UserRegistrationDTO dto) {

        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw  new IllegalArgumentException("Email already exists");
        }
        User user=new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setAvatarUrl(dto.getAvatarUrl());
        return userRepository.save(user);

    }

    @Override
    public String login(UserLoginDTO dto) {

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        if(authenticate.isAuthenticated())
            return jwtService.generateToken(dto.getEmail());

        return "fail";
    }

    @Override
    public User updateProfile(Long id, UserUpdateDTO dto ) {

        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found "));

        user.setName(dto.getName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User updateRole(Long id, String role, User currentUser) {

        if(currentUser.getRole()!= UserRole.ADMIN){
            throw new SecurityException("Unauthorized");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(UserRole.valueOf(role));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllStudents() {

        return userRepository.findAll();
    }
}
