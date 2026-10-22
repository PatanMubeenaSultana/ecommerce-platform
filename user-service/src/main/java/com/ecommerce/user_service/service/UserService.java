package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.LoginRequest;
import com.ecommerce.user_service.dto.LoginResponse;
import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.dto.UserResponse;
import com.ecommerce.user_service.entity.Role;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.UserAlreadyExistsException;
import com.ecommerce.user_service.repository.RoleRepository;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    public String registerUser(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow();

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(customerRole)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword());

        if (!passwordMatches) {
            throw new RuntimeException("Invalid Credentials");
        }

        String token =
                jwtService.generateToken(user.getEmail());

        return new LoginResponse(token);
    }

    public UserResponse getProfile(){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow();
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user){
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        return response;
    }
}
