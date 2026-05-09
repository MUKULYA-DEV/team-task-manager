package com.taskmanager.services;

import com.taskmanager.dto.AuthResponse;
import com.taskmanager.dto.LoginRequest;
import com.taskmanager.dto.RegisterRequest;
import com.taskmanager.models.Role;
import com.taskmanager.models.User;
import com.taskmanager.repositories.UserRepository;
import com.taskmanager.security.JwtUtil;
import com.taskmanager.security.UserPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        boolean firstAccount = userRepository.count() == 0;
        user.setRole(firstAccount ? Role.ADMIN : Role.MEMBER);
        userRepository.save(user);
        UserPrincipal principal =
                new UserPrincipal(
                        user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole());
        String token = jwtUtil.generateToken(principal);
        return AuthResponse.of(token, user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user =
                userRepository
                        .findByEmail(request.email())
                        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        UserPrincipal principal =
                new UserPrincipal(
                        user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole());
        return AuthResponse.of(jwtUtil.generateToken(principal), user.getEmail(), user.getRole());
    }
}
