package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.entity.Role;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.repository.RoleRepository;
import com.ecommerce.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    public String registerUser(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists";
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
}
