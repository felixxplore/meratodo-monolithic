package com.felix.meratodo.service;

import com.felix.meratodo.dto.UserLoginDTO;
import com.felix.meratodo.dto.UserRegistrationDTO;
import com.felix.meratodo.dto.UserUpdateDTO;
import com.felix.meratodo.model.User;
import java.util.List;

public interface UserService {

      User register(UserRegistrationDTO dto);

       String login(UserLoginDTO dto);

      User updateProfile(Long id, UserUpdateDTO dto);

      User updateRole(Long id, String role, User currentUser);

      List<User> getAllStudents();
}